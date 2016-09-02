package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Stretch;
import com.spanishcoders.model.Timetable;
import com.spanishcoders.repositories.AgendaRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Created by agustin on 2/07/16.
 */
public class AgendaServiceTests extends PelukaapUnitTest {

    @MockBean
    private AgendaRepository agendaRepository;

    @MockBean
    private WorkingDayService workingDayService;

    private AgendaService agendaService;

    @Before
    public void setUp() {
        agendaService = new AgendaService(agendaRepository, workingDayService);
    }

    @Test
    public void getFirstTenAvailableBlocksWithoutAgenda() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(null, null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksWithoutWorks() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(mockAgenda(), null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksWithEmptyAgenda() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(mockAgenda(), null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksWithEmptyWorks() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(mockAgenda(), Sets.newHashSet());
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksForOneWork() {
        Agenda agenda = mock(Agenda.class);
        given(workingDayService.getFirstTenAvailableBlocks(any(Agenda.class), any(Set.class))).willReturn(mockTenBlocks());
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(agenda, mockPublicWork());
        assertThat(blocks.size(), is(10));
    }

    @Test
    public void getFirstTenAvailableBlocksForAllPublicWorks() {
        Agenda agenda = mock(Agenda.class);
        given(workingDayService.getFirstTenAvailableBlocks(any(Agenda.class), any(Set.class))).willReturn(mockTenBlocks());
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(agenda, mockPublicWorks());
        assertThat(blocks.size(), is(10));
    }

    @Test
    public void getTodaysBlocksForExistingWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(true);
        Set<Block> blocks = Sets.newHashSet(mock(Block.class));
        given(agenda.getWorkingDayBlocks(any(LocalDate.class))).willReturn(blocks);
        Set<Block> todaysBlocks = agendaService.getTodaysBlocks(agenda);
        assertThat(todaysBlocks, is(blocks));
    }

    @Test
    public void getTodaysBlocksForNonExistingWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
        given(agenda.isNonWorkingDay(any(LocalDate.class))).willReturn(false);
        Stretch stretch = mock(Stretch.class);
        given(stretch.getStart()).willReturn(LocalTime.now());
        given(stretch.getEnd()).willReturn(LocalTime.now().plusHours(2));
        Timetable timetable = mock(Timetable.class);
        given(timetable.getStretches()).willReturn(Sets.newHashSet(stretch));
        given(agenda.getCurrentTimetable()).willReturn(timetable);
        given(agendaRepository.save(any(Agenda.class))).willAnswer(invocation -> invocation.getArguments()[0]);
        Set<Block> blocks = Sets.newHashSet(mock(Block.class));
        given(agenda.getWorkingDayBlocks(any(LocalDate.class))).willReturn(blocks);
        Set<Block> todaysBlocks = agendaService.getTodaysBlocks(agenda);
        assertThat(todaysBlocks, is(blocks));
    }

    @Test
    public void getTodaysBlocksForNonWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
        given(agenda.isNonWorkingDay(any(LocalDate.class))).willReturn(true);
        Set<Block> todaysBlocks = agendaService.getTodaysBlocks(agenda);
        assertThat(todaysBlocks, is(empty()));
    }
}