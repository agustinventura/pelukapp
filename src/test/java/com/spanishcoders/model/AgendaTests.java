package com.spanishcoders.model;

import static com.spanishcoders.TestDataFactory.agenda;
import static com.spanishcoders.TestDataFactory.hairdresser;
import static com.spanishcoders.TestDataFactory.publicWork;
import static com.spanishcoders.TestDataFactory.timetable;
import static com.spanishcoders.TestDataFactory.workingDay;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.OpeningDay;
import com.spanishcoders.agenda.OpeningHours;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.appointment.Appointment;
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
		final Agenda agenda = agenda();
		agenda.addWorkingDay(null);
	}

	@Test
	public void addWorkingDay() throws Exception {
		final Agenda agenda = agenda();
		final Timetable timetable = timetable();
		agenda.addTimetable(timetable);
		final Set<OpeningHours> openingHours = Sets.newHashSet();
		LocalDate day = agenda.getCurrentTimetable().getEndDate();
		while (openingHours.isEmpty()) {
			day = day.minusDays(1);
			openingHours.addAll(timetable.getOpeningHoursForDay(day));
		}
		agenda.addWorkingDay(day);
		assertThat(agenda.getWorkingDays().size(), is(1));
		assertThat(agenda.getWorkingDays().keySet(), contains(day));
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

	@Test(expected = IllegalStateException.class)
	public void addClosingDayWithPreviousAppointment() {
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable());
		agenda.addWorkingDay(agenda.getCurrentTimetable().getStartDate());
		final WorkingDay workingDay = agenda.getWorkingDays().get(agenda.getCurrentTimetable().getStartDate());
		final Block block = workingDay.getBlocks().iterator().next();
		final Appointment appointment = new Appointment(agenda.getHairdresser(), publicWork(), Sets.newHashSet(block),
				"");
		final LocalDate closingDay = appointment.getDate().toLocalDate();
		agenda.addClosingDay(closingDay);
		assertThat(agenda.getClosingDays(), contains(closingDay));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullClosingDay() {
		final Agenda agenda = agenda();
		agenda.addClosingDay(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void modifyTimetableNotInAgenda() {
		final Timetable timetable = getSummerTimetable();
		final Agenda agenda = agenda();
		agenda.modifyStartDate(LocalDate.now().minusMonths(1), timetable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void modifyTimetableStartDateInPast() {
		final LocalDate startDate = LocalDate.now().minusDays(1);
		final LocalDate summerEndDate = LocalDate.now().plusDays(1);
		final Timetable timetable = new Timetable(startDate, summerEndDate);
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		agenda.modifyStartDate(LocalDate.now().minusMonths(1), timetable);
	}

	@Test
	public void moveBackwardTimetableStartDateWithoutOverlap() {
		final Timetable timetable = getSummerTimetable();
		final LocalDate startDate = timetable.getStartDate();
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		LocalDate day = timetable.getStartDate().minusDays(1);
		while (!agenda.hasWorkingDay(day)) {
			day = day.plusDays(1);
			agenda.addWorkingDay(day);
		}
		final Set<Block> dayBlocks = agenda.getWorkingDayBlocks(day);
		agenda.modifyStartDate(startDate.minusMonths(1), timetable);
		assertThat(timetable.getStartDate(), is(startDate.minusMonths(1)));
		agenda.addWorkingDay(day.minusDays(7));
		final Set<Block> oneWeekBeforeDayBlocks = agenda.getWorkingDayBlocks(day.minusDays(7));
		assertThat(oneWeekBeforeDayBlocks.size(), is(dayBlocks.size()));
	}

	@Test
	public void moveBackwardTimetableStartDateWithOverlapWithoutAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate day = summerTimetable.getEndDate().plusDays(1);
		while (!agenda.hasWorkingDay(day)) {
			day = day.minusDays(1);
			agenda.addWorkingDay(day);
		}
		final Set<Block> summerTimetableDayBlocks = agenda.getWorkingDayBlocks(day);
		assertThat(summerTimetableDayBlocks.size(), is(13));
		final LocalDate newStartDate = winterTimetable.getStartDate().minusMonths(1);
		agenda.modifyStartDate(newStartDate, winterTimetable);
		assertThat(winterTimetable.getStartDate(), is(newStartDate));
		assertThat(summerTimetable.getEndDate(), is(newStartDate.minusDays(1)));
		final Set<Block> winterTimetableDayBlocks = agenda.getWorkingDayBlocks(day);
		assertThat(winterTimetableDayBlocks.size(), is(14));
	}

	@Test
	public void moveBackwardTimetableStartDateWithOverlapWithAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate day = summerTimetable.getEndDate();
		final Set<Block> blocks = Sets.newHashSet();
		while (blocks.isEmpty()) {
			agenda.addWorkingDay(day);
			blocks.addAll(agenda.getWorkingDayBlocks(day));
			day = day.minusDays(1);
		}
		day = day.plusDays(1);
		final Appointment appointment = new Appointment(hairdresser(), publicWork(),
				Sets.newHashSet(blocks.iterator().next()), "");
		final LocalDate oldStartDate = winterTimetable.getStartDate();
		assertThat(blocks.size(), is(13));
		agenda.modifyStartDate(oldStartDate.minusMonths(1), winterTimetable);
		assertThat(winterTimetable.getStartDate(), is(oldStartDate.minusMonths(1)));
		assertThat(summerTimetable.getEndDate(), is(oldStartDate.minusMonths(1).minusDays(1)));
		assertThat(agenda.getWorkingDays().get(appointment.getDate().toLocalDate()).hasValidAppointment(), is(true));
		final Set<Block> winterTimetableDayBlocks = agenda.getWorkingDayBlocks(day);
		assertThat(winterTimetableDayBlocks.size(), is(14));
	}

	@Test(expected = IllegalStateException.class)
	public void moveBackwardTimetableStartDateWithOverlapWithConflictedAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		int dayCount = 0;
		final Set<Block> blocks = Sets.newHashSet();
		while (blocks.isEmpty()) {
			agenda.addWorkingDay(summerTimetable.getEndDate().minusDays(dayCount));
			blocks.addAll(agenda.getWorkingDayBlocks(summerTimetable.getEndDate().minusDays(dayCount)));
			dayCount++;
		}
		final Block firstAfternoonBlock = blocks.stream().filter(block -> block.getStart().equals(LocalTime.of(20, 30)))
				.findFirst().get();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(),
				Sets.newHashSet(firstAfternoonBlock), "");
		agenda.modifyStartDate(winterTimetable.getStartDate().minusMonths(1), winterTimetable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void moveForwardThanEndDateTimetableStartDate() {
		final Timetable timetable = new Timetable(LocalDate.now().plusMonths(2), LocalDate.now().plusMonths(3));
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		agenda.modifyStartDate(LocalDate.now().plusMonths(4), timetable);
	}

	@Test
	public void moveForwardTimetableStartDateWithoutAppointments() {
		final Timetable timetable = getSummerTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		LocalDate day = timetable.getStartDate().plusMonths(1);
		while (!agenda.hasWorkingDay(day)) {
			day = day.minusDays(1);
			agenda.addWorkingDay(day);
		}
		final LocalDate oldStartDate = timetable.getStartDate();
		agenda.modifyStartDate(oldStartDate.plusMonths(1), timetable);
		assertThat(timetable.getStartDate(), is(oldStartDate.plusMonths(1)));
		assertThat(agenda.hasWorkingDay(day), is(false));
	}

	@Test
	public void moveForwardTimetableStartDateWithAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		int dayCount = 0;
		final Set<Block> blocks = Sets.newHashSet();
		while (blocks.isEmpty()) {
			agenda.addWorkingDay(winterTimetable.getEndDate().minusDays(dayCount));
			blocks.addAll(agenda.getWorkingDayBlocks(winterTimetable.getEndDate().minusDays(dayCount)));
			dayCount++;
		}
		final Appointment appointment = new Appointment(hairdresser(), publicWork(),
				Sets.newHashSet(blocks.iterator().next()), "");
		LocalDate day = winterTimetable.getStartDate().plusMonths(1);
		while (!agenda.hasWorkingDay(day)) {
			day = day.minusDays(1);
			agenda.addWorkingDay(day);
		}
		final LocalDate oldStartDate = winterTimetable.getStartDate();
		agenda.modifyStartDate(oldStartDate.plusMonths(1), winterTimetable);
		assertThat(winterTimetable.getStartDate(), is(oldStartDate.plusMonths(1)));
		assertThat(agenda.getWorkingDays().get(appointment.getDate().toLocalDate()).hasValidAppointment(), is(true));
		assertThat(agenda.hasWorkingDay(day), is(false));
	}

	@Test(expected = IllegalStateException.class)
	public void moveForwardTimetableStartDateWithConflictedAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate day = winterTimetable.getStartDate();
		final Set<Block> blocks = Sets.newHashSet();
		while (blocks.isEmpty()) {
			agenda.addWorkingDay(day);
			blocks.addAll(agenda.getWorkingDayBlocks(day));
			day = day.plusDays(1);
		}
		day = day.minusDays(1);
		final Block firstAfternoonBlock = blocks.stream().filter(block -> block.getStart() == LocalTime.of(17, 00))
				.findFirst().get();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(),
				Sets.newHashSet(firstAfternoonBlock), "");
		agenda.modifyStartDate(winterTimetable.getStartDate().plusMonths(1), winterTimetable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void modifyTimetableEndDateInPast() {
		final LocalDate startDate = LocalDate.now().minusMonths(1);
		final LocalDate summerEndDate = LocalDate.now().minusDays(1);
		final Timetable timetable = new Timetable(startDate, summerEndDate);
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		agenda.modifyEndDate(LocalDate.now().minusMonths(1), timetable);
	}

	@Test
	public void moveForwardTimetableEndDateWithoutOverlap() {
		final Timetable timetable = getSummerTimetable();
		final LocalDate oldEndDate = timetable.getEndDate();
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		LocalDate day = oldEndDate.plusDays(1);
		while (!agenda.hasWorkingDay(day)) {
			day = day.minusDays(1);
			agenda.addWorkingDay(day);
		}
		final Set<Block> dayBlocks = agenda.getWorkingDayBlocks(day);
		agenda.modifyEndDate(oldEndDate.plusMonths(1), timetable);
		assertThat(timetable.getEndDate(), is(oldEndDate.plusMonths(1)));
		agenda.addWorkingDay(day.plusDays(7));
		final Set<Block> oneWeekAfterDayBlocks = agenda.getWorkingDayBlocks(day.plusDays(7));
		assertThat(oneWeekAfterDayBlocks.size(), is(dayBlocks.size()));
	}

	@Test
	public void moveForwardTimetableEndDateWithOverlapWithoutAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate firstWinterDay = winterTimetable.getStartDate().minusDays(1);
		while (!agenda.hasWorkingDay(firstWinterDay)) {
			firstWinterDay = firstWinterDay.plusDays(1);
			agenda.addWorkingDay(firstWinterDay);
		}
		final Set<Block> winterTimetableDayBlocks = agenda.getWorkingDayBlocks(firstWinterDay);
		assertThat(winterTimetableDayBlocks.size(), is(14));
		final LocalDate newEndDate = summerTimetable.getEndDate().plusMonths(1);
		agenda.modifyEndDate(newEndDate, summerTimetable);
		assertThat(summerTimetable.getEndDate(), is(newEndDate));
		assertThat(winterTimetable.getStartDate(), is(newEndDate.plusDays(1)));
		final Set<Block> summerTimetableDayBlocks = agenda.getWorkingDayBlocks(firstWinterDay);
		assertThat(summerTimetableDayBlocks.size(), is(13));
	}

	@Test
	public void moveForwardTimetableEndDateWithOverlapWithAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate firstWinterDay = winterTimetable.getStartDate().minusDays(1);
		while (!agenda.hasWorkingDay(firstWinterDay)) {
			firstWinterDay = firstWinterDay.plusDays(1);
			agenda.addWorkingDay(firstWinterDay);
		}
		final Set<Block> winterTimetableDayBlocks = agenda.getWorkingDayBlocks(firstWinterDay);
		final Block firstMorningBlock = winterTimetableDayBlocks.stream()
				.filter(block -> block.getStart().equals(LocalTime.of(10, 30))).findFirst().get();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(), Sets.newHashSet(firstMorningBlock),
				"");
		final LocalDate oldEndDate = summerTimetable.getEndDate();
		assertThat(winterTimetableDayBlocks.size(), is(14));
		agenda.modifyEndDate(oldEndDate.plusMonths(1), summerTimetable);
		assertThat(summerTimetable.getEndDate(), is(oldEndDate.plusMonths(1)));
		assertThat(winterTimetable.getStartDate(), is(oldEndDate.plusMonths(1).plusDays(1)));
		assertThat(agenda.getWorkingDays().get(appointment.getDate().toLocalDate()).hasValidAppointment(), is(true));
		final Set<Block> summerTimetableDayBlocks = agenda.getWorkingDayBlocks(firstWinterDay);
		assertThat(summerTimetableDayBlocks.size(), is(13));
	}

	@Test(expected = IllegalStateException.class)
	public void moveForwardTimetableEndDateWithOverlapWithConflictedAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate firstWinterDay = winterTimetable.getStartDate().minusDays(1);
		while (!agenda.hasWorkingDay(firstWinterDay)) {
			firstWinterDay = firstWinterDay.plusDays(1);
			agenda.addWorkingDay(firstWinterDay);
		}
		final Set<Block> winterTimetableDayBlocks = agenda.getWorkingDayBlocks(firstWinterDay);
		final Block firstMorningBlock = winterTimetableDayBlocks.stream()
				.filter(block -> block.getStart().equals(LocalTime.of(9, 30))).findFirst().get();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(), Sets.newHashSet(firstMorningBlock),
				"");
		agenda.modifyEndDate(summerTimetable.getEndDate().plusMonths(1), summerTimetable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void moveBackwardThanStartDateTimetableEndDate() {
		final Timetable timetable = new Timetable(LocalDate.now().plusMonths(2), LocalDate.now().plusMonths(3));
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		agenda.modifyEndDate(LocalDate.now(), timetable);
	}

	@Test
	public void moveBackwardTimetableEndDateWithoutAppointments() {
		final Timetable timetable = getSummerTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(timetable);
		LocalDate day = timetable.getEndDate().minusMonths(1);
		while (!agenda.hasWorkingDay(day)) {
			day = day.plusDays(1);
			agenda.addWorkingDay(day);
		}
		final LocalDate oldEndDate = timetable.getEndDate();
		agenda.modifyEndDate(oldEndDate.minusMonths(1), timetable);
		assertThat(timetable.getEndDate(), is(oldEndDate.minusMonths(1)));
		assertThat(agenda.hasWorkingDay(day), is(false));
	}

	@Test
	public void moveBackwardTimetableEndDateWithAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate lastWinterDay = winterTimetable.getEndDate().plusDays(1);
		while (!agenda.hasWorkingDay(lastWinterDay)) {
			lastWinterDay = lastWinterDay.minusDays(1);
			agenda.addWorkingDay(lastWinterDay);
		}
		final Set<Block> winterTimetableDayBlocks = agenda.getWorkingDayBlocks(lastWinterDay);
		final Block firstBlock = winterTimetableDayBlocks.iterator().next();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(), Sets.newHashSet(firstBlock), "");
		LocalDate lastSummerDay = summerTimetable.getEndDate().minusMonths(1);
		while (!agenda.hasWorkingDay(lastSummerDay)) {
			lastSummerDay = lastSummerDay.plusDays(1);
			agenda.addWorkingDay(lastSummerDay);
		}
		final LocalDate oldEndDate = summerTimetable.getEndDate();
		agenda.modifyEndDate(oldEndDate.minusMonths(1), summerTimetable);
		assertThat(summerTimetable.getEndDate(), is(oldEndDate.minusMonths(1)));
		assertThat(agenda.getWorkingDays().get(appointment.getDate().toLocalDate()).hasValidAppointment(), is(true));
		assertThat(agenda.hasWorkingDay(lastSummerDay), is(false));
	}

	@Test(expected = IllegalStateException.class)
	public void moveBackwardTimetableEndDateWithConflictedAppointments() {
		final Timetable summerTimetable = getSummerTimetable();
		final Timetable winterTimetable = getWinterTimetable();
		final Agenda agenda = agenda();
		agenda.addTimetable(summerTimetable);
		agenda.addTimetable(winterTimetable);
		LocalDate lastSummerDay = summerTimetable.getEndDate().plusDays(1);
		final Set<Block> blocks = Sets.newHashSet();
		while (blocks.isEmpty()) {
			lastSummerDay = lastSummerDay.minusDays(1);
			agenda.addWorkingDay(lastSummerDay);
			blocks.addAll(agenda.getWorkingDayBlocks(lastSummerDay));
		}
		final Block lastAfternoonBlock = blocks.stream().filter(block -> block.getStart().equals(LocalTime.of(20, 30)))
				.findFirst().get();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(),
				Sets.newHashSet(lastAfternoonBlock), "");
		agenda.modifyEndDate(summerTimetable.getEndDate().minusMonths(1), summerTimetable);
	}

	private Timetable getSummerTimetable() {
		final LocalDate summerStartDate = LocalDate.of(LocalDate.now().getYear() + 1, 6, 1);
		final LocalDate summerEndDate = LocalDate.of(LocalDate.now().getYear() + 1, 9, 30);
		final Timetable summerTimetable = new Timetable(summerStartDate, summerEndDate);
		for (final DayOfWeek weekDay : DayOfWeek.values()) {
			final OpeningHours morning = new OpeningHours(LocalTime.of(10, 30), LocalTime.of(14, 00));
			final OpeningHours afternoon = new OpeningHours(LocalTime.of(18, 00), LocalTime.of(21, 00));
			if (weekDay != DayOfWeek.SUNDAY && weekDay != DayOfWeek.SATURDAY) {
				summerTimetable.addOpeningDay(weekDay, morning, afternoon);
			}
		}
		return summerTimetable;
	}

	private Timetable getWinterTimetable() {
		final LocalDate winterStartDate = LocalDate.of(LocalDate.now().getYear() + 1, 10, 1);
		final LocalDate winterEndDate = LocalDate.of(LocalDate.now().getYear() + 2, 5, 31);
		final Timetable summerTimetable = new Timetable(winterStartDate, winterEndDate);
		for (final DayOfWeek weekDay : DayOfWeek.values()) {
			final OpeningHours morning = new OpeningHours(LocalTime.of(9, 30), LocalTime.of(13, 30));
			final OpeningHours afternoon = new OpeningHours(LocalTime.of(17, 00), LocalTime.of(20, 00));
			if (weekDay == DayOfWeek.SATURDAY) {
				summerTimetable.addOpeningDay(weekDay, morning);
			} else if (weekDay != DayOfWeek.SUNDAY) {
				summerTimetable.addOpeningDay(weekDay, morning, afternoon);
			}
		}
		return summerTimetable;
	}
}