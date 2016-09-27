package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.*;
import com.spanishcoders.repositories.AgendaRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.SortedMap;

import static com.spanishcoders.TestDataFactory.mockAgenda;
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
        given(stretch.getStartTime()).willReturn(LocalTime.now());
        given(stretch.getEndTime()).willReturn(LocalTime.now().plusHours(2));
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

    @Test
    public void getAvailableBlocksWithoutAgenda() {
        Set<Block> blocks = agendaService.getAvailableBlocks(null, null, null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getAvailableBlocksWithoutWorks() {
        Set<Block> blocks = agendaService.getAvailableBlocks(mock(Agenda.class), null, null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getAvailableBlocksWithEmptyWorks() {
        Set<Block> blocks = agendaService.getAvailableBlocks(mockAgenda(), Sets.newHashSet(), null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getAvailableBlocksWithoutDate() {
        Set<Block> blocks = agendaService.getAvailableBlocks(mockAgenda(), Sets.newHashSet(mock(Work.class)), null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getAvailableBlocksForNonWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
        given(agenda.isNonWorkingDay(any(LocalDate.class))).willReturn(true);
        Set<Block> blocks = agendaService.getAvailableBlocks(agenda, Sets.newHashSet(mock(Work.class)), LocalDate.now());
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getAvailableBlocksExistingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(true);
        WorkingDay workingDay = mock(WorkingDay.class);
        SortedMap<LocalDate, WorkingDay> workingDays = mock(SortedMap.class);
        given(agenda.getWorkingDays()).willReturn(workingDays);
        given(workingDays.get(any(LocalDate.class))).willReturn(workingDay);
        Set<Block> blocks = Sets.newHashSet(mock(Block.class));
        given(workingDay.getAvailableBlocks(any(Set.class))).willReturn(blocks);
        Set<Block> returnedBlocks = agendaService.getAvailableBlocks(agenda, Sets.newHashSet(mock(Work.class)), LocalDate.now());
        assertThat(returnedBlocks, is(blocks));
    }

    @Test
    public void getAvailableBlocksNonExistingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
        given(agenda.isNonWorkingDay(any(LocalDate.class))).willReturn(false);
        Stretch stretch = mock(Stretch.class);
        given(stretch.getStartTime()).willReturn(LocalTime.now().minusHours(1));
        given(stretch.getEndTime()).willReturn(LocalTime.now().plusHours(1));
        Set<Stretch> stretches = Sets.newHashSet(stretch);
        Timetable timetable = mock(Timetable.class);
        given(agenda.getCurrentTimetable()).willReturn(timetable);
        given(timetable.getStretches()).willReturn(stretches);
        given(agendaRepository.save(any(Agenda.class))).willReturn(agenda);
        WorkingDay workingDay = mock(WorkingDay.class);
        SortedMap<LocalDate, WorkingDay> workingDays = mock(SortedMap.class);
        given(agenda.getWorkingDays()).willReturn(workingDays);
        given(workingDays.get(any(LocalDate.class))).willReturn(workingDay);
        Set<Block> blocks = Sets.newHashSet(mock(Block.class));
        given(workingDay.getAvailableBlocks(any(Set.class))).willReturn(blocks);
        Set<Block> returnedBlocks = agendaService.getAvailableBlocks(agenda, Sets.newHashSet(mock(Work.class)), LocalDate.now());
        assertThat(returnedBlocks, is(blocks));
    }
}