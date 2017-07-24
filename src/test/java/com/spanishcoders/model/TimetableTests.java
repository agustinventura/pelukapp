package com.spanishcoders.model;

import static com.spanishcoders.TestDataFactory.timetable;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import org.junit.Test;

import com.spanishcoders.agenda.OpeningDay;
import com.spanishcoders.agenda.OpeningHours;
import com.spanishcoders.agenda.Timetable;

public class TimetableTests {

	@Test(expected = IllegalArgumentException.class)
	public void createTimetableWithStartDateAfterEndDate() {
		new Timetable(LocalDate.now().plusDays(1L), LocalDate.now());
	}

	@Test
	public void createTimetable() {
		final LocalDate startDay = LocalDate.now();
		final LocalDate endDay = LocalDate.now().plusMonths(6);
		final Timetable timetable = new Timetable(startDay, endDay);
		assertThat(timetable.getStartDate(), is(startDay));
		assertThat(timetable.getEndDate(), is(endDay));
		assertThat(timetable.getOpeningDays(), is(empty()));
	}

	@Test
	public void addOpeningDay() {
		final Timetable timetable = timetable();
		final Set<OpeningDay> currentOpeningDays = timetable.getOpeningDays();
		final OpeningDay firstOpeningDay = currentOpeningDays.iterator().next();
		final Set<OpeningHours> openingHours = firstOpeningDay.getOpeningHours();
		final DayOfWeek nextDayOfWeek = firstOpeningDay.getWeekDay().plus(1);
		timetable.addOpeningDay(nextDayOfWeek, openingHours.toArray(new OpeningHours[openingHours.size()]));
		assertThat(timetable.getOpeningDays().contains(firstOpeningDay), is(true));
		assertThat(timetable.getOpeningDays().stream()
				.anyMatch(addedOpeningDay -> addedOpeningDay.getWeekDay() == nextDayOfWeek), is(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullOpeningDay() {
		final Timetable timetable = timetable();
		timetable.addOpeningDay(null);
	}

	@Test
	public void overlapsWithNullTimetable() {
		final Timetable timetable = timetable();
		assertThat(timetable.overlaps(null), is(false));
	}

	@Test
	public void overlapsWithNonOverlappingTimetable() {
		final Timetable timetable = timetable();
		final LocalDate endDate = timetable.getEndDate();
		final Timetable nonOverlappingTimetable = new Timetable(endDate.plusDays(1L), endDate.plusDays(2L));
		assertThat(timetable.overlaps(nonOverlappingTimetable), is(false));
	}

	@Test
	public void overlapsWithOverlappingStartDateTimetable() {
		final Timetable timetable = timetable();
		final LocalDate endDate = timetable.getEndDate();
		final Timetable overlappingTimetable = new Timetable(endDate, endDate.plusDays(1L));
		assertThat(timetable.overlaps(overlappingTimetable), is(true));
	}

	@Test
	public void overlapsWithOverlappingEndDateTimetable() {
		final Timetable timetable = timetable();
		final LocalDate startDate = timetable.getStartDate();
		final Timetable overlappingTimetable = new Timetable(startDate.minusDays(1L), startDate);
		assertThat(timetable.overlaps(overlappingTimetable), is(true));
	}

	@Test
	public void overlapsWithOverlappingStartAndEndDateTimetable() {
		final Timetable timetable = timetable();
		assertThat(timetable.overlaps(timetable), is(true));
	}

	@Test
	public void containsNullDate() {
		final Timetable timetable = timetable();
		assertThat(timetable.contains(null), is(false));
	}

	@Test
	public void containsDate() {
		final Timetable timetable = timetable();
		assertThat(timetable.contains(LocalDate.now()), is(true));
	}

	@Test
	public void notContainsDate() {
		final Timetable timetable = timetable();
		assertThat(timetable.contains(LocalDate.now().minusYears(100L)), is(false));
	}

	@Test
	public void getOpeningHoursForExistingDay() {
		final Timetable timetable = timetable();
		final Set<OpeningHours> openingHours = timetable.getOpeningHoursForDay(LocalDate.now());
		assertThat(openingHours, is(not(empty())));
	}

	@Test
	public void getOpeningHoursForNonExistingDay() {
		final Timetable timetable = timetable();
		final Set<OpeningHours> openingHours = timetable.getOpeningHoursForDay(LocalDate.now().minusDays(1L));
		assertThat(openingHours, is(empty()));
	}

	@Test
	public void getOpeningHoursForNullExistingDay() {
		final Timetable timetable = timetable();
		final Set<OpeningHours> openingHours = timetable.getOpeningHoursForDay(null);
		assertThat(openingHours, is(empty()));
	}
}
