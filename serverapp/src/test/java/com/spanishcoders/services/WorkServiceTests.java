package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Role;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import com.spanishcoders.repositories.WorkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
public class WorkServiceTests {

    @MockBean
    private WorkRepository workRepository;

    private WorkService workService;

    @Before
    public void setUp() {
        workService = new WorkService(workRepository);
    }

    private Set<Work> mockPrivateWorks() {
        Work regulation = new Work("Regulacion", 30, WorkKind.PRIVATE);
        return Sets.newHashSet(regulation);
    }

    private Set<Work> mockPublicWorks() {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        Work shave = new Work("Afeitado", 30, WorkKind.PUBLIC);
        return Sets.newHashSet(cut, shave);
    }

    @Test
    public void getAvailableWorksForWorker() throws Exception {
        Set<Work> publicWorks = mockPublicWorks();
        Set<Work> privateWorks = mockPrivateWorks();
        given(workRepository.findAll()).willReturn(Sets.union(publicWorks, privateWorks));
        Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet(Role.WORKER.getGrantedAuthority()));
        assertThat(availableWorks, hasSize(3));
    }

    @Test
    public void getAvailableWorksForClient() throws Exception {
        Set<Work> publicWorks = mockPublicWorks();
        given(workRepository.findByKind(WorkKind.PUBLIC)).willReturn(publicWorks);
        Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet(Role.CLIENT.getGrantedAuthority()));
        assertThat(availableWorks, hasSize(2));
    }

    @Test
    public void getWorksById() {
        Set<Work> publicWorks = mockPublicWorks();
        given(workRepository.findAll(any(Collection.class))).willReturn(publicWorks);
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