package com.spanishcoders.model;

import static com.spanishcoders.TestDataFactory.mockTimetable;
import static com.spanishcoders.TestDataFactory.mockWorkingDay;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

public class AgendaTests {

	@Test(expected = IllegalArgumentException.class)
	public void addNullWorkingDay() throws Exception {
		final Agenda agenda = new Agenda();
		agenda.addWorkingDay(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullDateWorkingDay() throws Exception {
		final WorkingDay workingDay = mockWorkingDay();
		workingDay.setDate(null);
		final Agenda agenda = new Agenda();
		agenda.addWorkingDay(workingDay);
	}

	@Test
	public void addWorkingDay() throws Exception {
		final WorkingDay workingDay = mockWorkingDay();
		final Agenda agenda = new Agenda();
		agenda.addWorkingDay(workingDay);
		assertThat(agenda.getWorkingDays().size(), is(1));
		assertThat(agenda.getWorkingDays().values(), contains(workingDay));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullTimetable() throws Exception {
		final Agenda agenda = new Agenda();
		agenda.addTimetable(null);
	}

	@Test
	public void addTimetable() throws Exception {
		final Timetable timetable = mockTimetable();
		final Agenda agenda = new Agenda();
		agenda.addTimetable(timetable);
		assertThat(agenda.getTimetables().size(), is(1));
		assertThat(agenda.getTimetables(), contains(timetable));
	}

	@Test
	public void getCurrentTimetable() throws Exception {
		final LocalDate aMonthAgo = LocalDate.now().minusMonths(1);
		final LocalDate nextMonth = LocalDate.now().plusMonths(1);
		final Timetable current = new Timetable(aMonthAgo, nextMonth);
		final LocalDate pastYearStartDate = aMonthAgo.minusYears(1);
		final LocalDate pastYearEndDate = nextMonth.plusYears(1);
		final Timetable past = new Timetable(pastYearStartDate, pastYearEndDate);
		final Agenda agenda = new Agenda();
		agenda.addTimetable(current);
		agenda.addTimetable(past);
		final Timetable currentTimetable = agenda.getCurrentTimetable();
		assertThat(currentTimetable, is(current));
	}

	@Test
	public void getWorkingDayBlocksExistingWorkingDay() {
		final Agenda agenda = new Agenda();
		final WorkingDay workingDay = new WorkingDay();
		workingDay.setDate(LocalDate.now());
		final Block block = new Block();
		block.setStart(LocalTime.now());
		block.setWorkingDay(workingDay);
		final SortedSet<Block> blocks = Sets.newTreeSet();
		blocks.add(block);
		workingDay.setBlocks(blocks);
		agenda.getWorkingDays().put(LocalDate.now(), workingDay);
		final Set<Block> workingDayBlocks = agenda.getWorkingDayBlocks(LocalDate.now());
		assertThat(workingDayBlocks, is(blocks));
	}

	@Test
	public void getWorkingDayBlocksNonExistingWorkingDay() {
		final Agenda agenda = new Agenda();
		final Set<Block> workingDayBlocks = agenda.getWorkingDayBlocks(LocalDate.now());
		assertThat(workingDayBlocks, is(empty()));
	}

	@Test
	public void addClosingDay() {
		final Agenda agenda = new Agenda();
		final LocalDate closingDay = LocalDate.now();
		agenda.addClosingDay(closingDay);
		assertThat(agenda.getClosingDays(), contains(closingDay));
	}

	@Test
	public void addNullClosingDay() {
		final Agenda agenda = new Agenda();
		agenda.addClosingDay(null);
		assertThat(agenda.getClosingDays(), is(empty()));
	}
}