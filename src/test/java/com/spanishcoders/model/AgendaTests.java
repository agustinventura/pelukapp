package com.spanishcoders.model;

import static com.spanishcoders.TestDataFactory.agenda;
import static com.spanishcoders.TestDataFactory.timetable;
import static com.spanishcoders.TestDataFactory.workingDay;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.Test;

import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.OpeningDay;
import com.spanishcoders.agenda.OpeningHours;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

public class AgendaTests {

	@Test(expected = IllegalArgumentException.class)
	public void addNullWorkingDay() throws Exception {
		final Agenda agenda = agenda();
		agenda.addWorkingDay(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullDateWorkingDay() throws Exception {
		final WorkingDay workingDay = workingDay();
		workingDay.setDate(null);
		final Agenda agenda = agenda();
		agenda.addWorkingDay(workingDay);
	}

	@Test
	public void addWorkingDay() throws Exception {
		final WorkingDay workingDay = workingDay();
		final Agenda agenda = agenda();
		agenda.addWorkingDay(workingDay);
		assertThat(agenda.getWorkingDays().size(), is(1));
		assertThat(agenda.getWorkingDays().values(), contains(workingDay));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullTimetable() throws Exception {
		final Agenda agenda = agenda();
		agenda.addTimetable(null);
	}

	@Test
	public void addTimetable() throws Exception {
		final Timetable timetable = timetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		assertThat(agenda.getTimetables().size(), is(1));
		assertThat(agenda.getTimetables(), contains(timetable));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addOverlappingTimetable() {
		final Agenda agenda = agenda();
		final Timetable timetable = timetable();
		agenda.addTimetable(timetable);
		agenda.addTimetable(timetable);
	}

	@Test
	public void addNonOverlappingTimetable() {
		final Agenda agenda = agenda();
		final Timetable timetable = timetable();
		agenda.addTimetable(timetable);
		assertThat(agenda.getTimetables().size(), is(1));
		assertThat(agenda.getTimetables(), contains(timetable));
		final LocalDate timetableEndDate = timetable.getEndDate();
		final Timetable newTimetable = new Timetable(timetableEndDate.plusDays(1L), timetableEndDate.plusMonths(6L));
		for (final OpeningDay openingDay : timetable.getOpeningDays()) {
			newTimetable.addOpeningDay(openingDay.getWeekDay(),
					openingDay.getOpeningHours().toArray(new OpeningHours[openingDay.getOpeningHours().size()]));
		}
		agenda.addTimetable(newTimetable);
		assertThat(agenda.getTimetables().size(), is(2));
		assertThat(agenda.getTimetables().contains(timetable), is(true));
		assertThat(agenda.getTimetables().contains(newTimetable), is(true));
	}

	@Test
	public void getCurrentTimetable() throws Exception {
		final Timetable current = timetable();
		final Timetable past = new Timetable(current.getStartDate().minusYears(1L),
				current.getStartDate().minusDays(1L));
		final Agenda agenda = agenda();
		agenda.addTimetable(current);
		agenda.addTimetable(past);
		final Timetable currentTimetable = agenda.getCurrentTimetable();
		assertThat(currentTimetable, is(current));
	}

	@Test
	public void getWorkingDayBlocksExistingWorkingDay() {
		final WorkingDay workingDay = workingDay();
		final Set<Block> blocks = workingDay.getBlocks();
		workingDay.getAgenda().addWorkingDay(workingDay);
		final Set<Block> workingDayBlocks = workingDay.getAgenda().getWorkingDayBlocks(workingDay.getDate());
		assertThat(workingDayBlocks, is(blocks));
	}

	@Test
	public void getWorkingDayBlocksNonExistingWorkingDay() {
		final Agenda agenda = agenda();
		final Set<Block> workingDayBlocks = agenda.getWorkingDayBlocks(LocalDate.now());
		assertThat(workingDayBlocks, is(empty()));
	}

	@Test
	public void addClosingDay() {
		final Agenda agenda = agenda();
		final LocalDate closingDay = LocalDate.now();
		agenda.addClosingDay(closingDay);
		assertThat(agenda.getClosingDays(), contains(closingDay));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullClosingDay() {
		final Agenda agenda = agenda();
		agenda.addClosingDay(null);
	}
}