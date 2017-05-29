package com.spanishcoders.work;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.Role;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkRepository;
import com.spanishcoders.work.WorkService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.mockAllWorks;
import static com.spanishcoders.TestDataFactory.mockPublicWorks;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

public class WorkServiceTests extends PelukaapUnitTest {

    @MockBean
    private WorkRepository workRepository;

    private WorkService workService;

    @Before
    public void setUp() {
        workService = new WorkService(workRepository);
    }

    @Test
    public void getAvailableWorksForWorker() throws Exception {
        given(workRepository.findAll()).willReturn(mockAllWorks());
        Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet(Role.WORKER.getGrantedAuthority()));
        assertThat(availableWorks, hasSize(3));
    }

    @Test
    public void getAvailableWorksForClient() throws Exception {
        Set<Work> publicWorks = mockPublicWorks();
        given(workRepository.findByKind(WorkKind.PUBLIC)).willReturn(publicWorks);
        Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet(Role.CLIENT.getGrantedAuthority()));
        assertThat(availableWorks, hasSize(publicWorks.size()));
        assertThat(availableWorks, is(publicWorks));
    }

    @Test
    public void getWorksById() {
        given(workRepository.findAll(any(Collection.class))).willReturn(mockPublicWorks());
        Set<Work> works = workService.get(Sets.newHashSet(1, 2));
        assertThat(works, hasSize(2));
    }

    @Test
    public void getWorksByEmptyId() {
        Set<Work> works = workService.get(Sets.newHashSet());
        assertThat(works, is(empty()));
    }

    @Test
    public void getWorksByNullId() {
        Set<Work> works = workService.get(null);
        assertThat(works, is(empty()));
    }

}