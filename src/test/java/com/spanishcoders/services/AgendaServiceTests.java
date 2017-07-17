package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.AgendaRepository;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.agenda.Stretch;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.workingday.block.Block;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

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

    private AgendaService agendaService;

    @Before
    public void setUp() {
        agendaService = new AgendaService(agendaRepository);
    }

    @Test
    public void getDayBlocksWithoutAgenda() {
        Set<Block> dayBlocks = agendaService.getDayBlocks(null, LocalDate.now());
        assertThat(dayBlocks, is(empty()));
    }

    @Test
    public void getDayBlocksWithoutDay() {
        Set<Block> dayBlocks = agendaService.getDayBlocks(mock(Agenda.class), null);
        assertThat(dayBlocks, is(empty()));
    }

    @Test
    public void getDayBlocksForExistingWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(true);
        Set<Block> blocks = Sets.newHashSet(mock(Block.class));
        given(agenda.getWorkingDayBlocks(any(LocalDate.class))).willReturn(blocks);
        Set<Block> todaysBlocks = agendaService.getDayBlocks(agenda, LocalDate.now());
        assertThat(todaysBlocks, is(blocks));
    }

    @Test
    public void getDayBlocksForNonExistingWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
        given(agenda.isClosingDay(any(LocalDate.class))).willReturn(false);
        Stretch stretch = mock(Stretch.class);
        given(stretch.getStartTime()).willReturn(LocalTime.now());
        given(stretch.getEndTime()).willReturn(LocalTime.now().plusHours(2));
        Timetable timetable = mock(Timetable.class);
        given(timetable.getStretches()).willReturn(Sets.newHashSet(stretch));
        given(agenda.getCurrentTimetable()).willReturn(timetable);
        given(agendaRepository.save(any(Agenda.class))).willAnswer(invocation -> invocation.getArguments()[0]);
        Set<Block> blocks = Sets.newHashSet(mock(Block.class));
        given(agenda.getWorkingDayBlocks(any(LocalDate.class))).willReturn(blocks);
        Set<Block> todaysBlocks = agendaService.getDayBlocks(agenda, LocalDate.now());
        assertThat(todaysBlocks, is(blocks));
    }

    @Test
    public void getDayBlocksForNonWorkingDay() {
        Agenda agenda = mock(Agenda.class);
        given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
        given(agenda.isClosingDay(any(LocalDate.class))).willReturn(true);
        Set<Block> todaysBlocks = agendaService.getDayBlocks(agenda, LocalDate.now());
        assertThat(todaysBlocks, is(empty()));
    }
}