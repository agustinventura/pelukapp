package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.WorkingDay;
import com.spanishcoders.repositories.WorkingDayRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;
import java.util.SortedSet;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * Created by agustin on 6/07/16.
 */
public class WorkingDayServiceTests extends PelukaapUnitTest {

    @MockBean
    private WorkingDayRepository workingDayRepository;

    private WorkingDayService workingDayService;

    @Before
    public void setUp() {
        this.workingDayService = new WorkingDayService(workingDayRepository);
    }

    @Test
    public void saveNullWorkingDay() throws Exception {
        assertThat(workingDayService.save(null), nullValue());
    }

    @Test
    public void saveWorkingDay() {
        WorkingDay workingDay = mockWorkingDay();
        given(workingDayRepository.save(workingDay)).willReturn(workingDay);
        assertThat(workingDayService.save(workingDay), is(workingDay));
    }

    @Test
    public void getFirstTenAvailableBlocksForOneWorkWithNextDays() throws Exception {
        WorkingDay workingDay = Mockito.mock(WorkingDay.class);
        given(workingDay.getAvailableBlocks(any(Set.class))).willReturn(mockTenBlocks());
        SortedSet<WorkingDay> nextWorkingDays = Sets.newTreeSet();
        nextWorkingDays.add(workingDay);
        given(workingDayRepository.getNextWorkingDays(any(Agenda.class))).willReturn(nextWorkingDays);
        Agenda agenda = Mockito.mock(Agenda.class);
        Set<Block> availableBlocks = workingDayService.getFirstTenAvailableBlocks(agenda, mockPublicWork());
        assertThat(availableBlocks.size(), is(10));
    }

    @Test
    public void getFirstTenAvailableBlocksForOneWorkWithoutNextDays() throws Exception {
        SortedSet<WorkingDay> nextWorkingDays = Sets.newTreeSet();
        given(workingDayRepository.getNextWorkingDays(any(Agenda.class))).willReturn(nextWorkingDays);
        Agenda agenda = mockFullAgenda();
        given(workingDayRepository.save(any(WorkingDay.class))).willAnswer(invocation -> invocation.getArguments()[0]);
        Set<Block> availableBlocks = workingDayService.getFirstTenAvailableBlocks(agenda, mockPublicWork());
        assertThat(availableBlocks.size(), is(10));
    }
}