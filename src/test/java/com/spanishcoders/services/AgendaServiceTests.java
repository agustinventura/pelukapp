package com.spanishcoders.services;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.AgendaRepository;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.agenda.OpeningDay;
import com.spanishcoders.agenda.OpeningHours;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.workingday.block.Block;

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
		final Set<Block> dayBlocks = agendaService.getDayBlocks(null, LocalDate.now());
		assertThat(dayBlocks, is(empty()));
	}

	@Test
	public void getDayBlocksWithoutDay() {
		final Set<Block> dayBlocks = agendaService.getDayBlocks(mock(Agenda.class), null);
		assertThat(dayBlocks, is(empty()));
	}

	@Test
	public void getDayBlocksForExistingWorkingDay() {
		final Agenda agenda = mock(Agenda.class);
		given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(true);
		final Set<Block> blocks = Sets.newHashSet(mock(Block.class));
		given(agenda.getWorkingDayBlocks(any(LocalDate.class))).willReturn(blocks);
		final Set<Block> todaysBlocks = agendaService.getDayBlocks(agenda, LocalDate.now());
		assertThat(todaysBlocks, is(blocks));
	}

	@Test
	public void getDayBlocksForNonExistingWorkingDay() {
		final Agenda agenda = mock(Agenda.class);
		given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
		given(agenda.isClosingDay(any(LocalDate.class))).willReturn(false);
		final OpeningHours openingHours = mock(OpeningHours.class);
		given(openingHours.getStartTime()).willReturn(LocalTime.now());
		given(openingHours.getEndTime()).willReturn(LocalTime.now().plusHours(2));
		final OpeningDay openingDay = mock(OpeningDay.class);
		given(openingDay.getOpeningHours()).willReturn(Sets.newHashSet(openingHours));
		given(openingDay.getWeekDay()).willReturn(LocalDate.now().getDayOfWeek());
		final Timetable timetable = mock(Timetable.class);
		given(timetable.getOpeningDays()).willReturn(Sets.newHashSet(openingDay));
		given(agenda.getCurrentTimetable()).willReturn(timetable);
		given(agendaRepository.save(any(Agenda.class))).willAnswer(invocation -> invocation.getArguments()[0]);
		final Set<Block> blocks = Sets.newHashSet(mock(Block.class));
		given(agenda.getWorkingDayBlocks(any(LocalDate.class))).willReturn(blocks);
		final Set<Block> todaysBlocks = agendaService.getDayBlocks(agenda, LocalDate.now());
		assertThat(todaysBlocks, is(blocks));
	}

	@Test
	public void getDayBlocksForNonWorkingDay() {
		final Agenda agenda = mock(Agenda.class);
		given(agenda.hasWorkingDay(any(LocalDate.class))).willReturn(false);
		given(agenda.isClosingDay(any(LocalDate.class))).willReturn(true);
		final Set<Block> todaysBlocks = agendaService.getDayBlocks(agenda, LocalDate.now());
		assertThat(todaysBlocks, is(empty()));
	}
}