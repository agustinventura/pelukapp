package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Role;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import com.spanishcoders.repositories.WorkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkServiceTest {

    @MockBean
    private WorkRepository workRepository;

    @Autowired
    private WorkService workService;

    @Before
    public void setUp() throws Exception {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        Work shave = new Work("Afeitado", 30, WorkKind.PUBLIC);
        Work regulation = new Work("Regulacion", 30, WorkKind.PRIVATE);
        given(workRepository.findAll()).willReturn(Sets.newHashSet(cut, shave, regulation));
        given(workRepository.findByKind(WorkKind.PUBLIC)).willReturn(Sets.newHashSet(cut, shave));
    }

    @Test
    public void getAvailableWorksForWorker() throws Exception {
        Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet(Role.WORKER.getGrantedAuthority()));
        assertThat(availableWorks, hasSize(3));
    }

    @Test
    public void getAvailableWorksForClient() throws Exception {
        Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet(Role.CLIENT.getGrantedAuthority()));
        assertThat(availableWorks, hasSize(2));
    }

}