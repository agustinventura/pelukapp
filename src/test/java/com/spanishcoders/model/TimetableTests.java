package com.spanishcoders.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.Test;

import com.spanishcoders.agenda.Stretch;
import com.spanishcoders.agenda.Timetable;

public class TimetableTests {

	@Test(expected = IllegalArgumentException.class)
	public void createTimetableWithNullStretches() {
		new Timetable(LocalDate.now(), LocalDate.now(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createTimetableWithEmptyStretches() {
		new Timetable(LocalDate.now(), LocalDate.now(), new Stretch[0]);
	}

	@Test
	public void createTimetableWithStretch() {
		final LocalDate startDay = LocalDate.now();
		final LocalDate endDay = LocalDate.now().plusMonths(6);
		final Stretch stretch = new Stretch();
		final Timetable timetable = new Timetable(startDay, endDay, stretch);
		assertThat(timetable.getStartDay(), is(startDay));
		assertThat(timetable.getEndDay(), is(endDay));
		assertThat(timetable.getStretches(), contains(stretch));
	}

	@Test
	public void addStretch() {
		final Timetable timetable = new Timetable();
		final Stretch stretch = new Stretch();
		timetable.addStretch(stretch);
		assertThat(timetable.getStretches(), contains(stretch));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNullStretch() {
		final Timetable timetable = new Timetable();
		timetable.addStretch(null);
	}

	@Test
	public void overlapsWithNullTimetable() {
		final Timetable timetable = new Timetable();
		assertThat(timetable.overlaps(null), is(false));
	}

	@Test
	public void overlapsWithNonOverlappingTimetable() {
		final Timetable timetable = new Timetable(LocalDate.now().minusDays(2L), LocalDate.now().minusDays(1L),
				new Stretch());
		final Timetable nonOverlappingTimetable = new Timetable(LocalDate.now(), LocalDate.now().plusDays(1L),
				new Stretch());
		assertThat(timetable.overlaps(nonOverlappingTimetable), is(false));
	}

	@Test
	public void overlapsWithOverlappingStartDateTimetable() {
		final Timetable timetable = new Timetable(LocalDate.now().minusDays(1L), LocalDate.now(), new Stretch());
		final Timetable overlappingTimetable = new Timetable(LocalDate.now(), LocalDate.now().plusDays(1L),
				new Stretch());
		assertThat(timetable.overlaps(overlappingTimetable), is(true));
	}

	@Test
	public void overlapsWithOverlappingEndDateTimetable() {
		final Timetable timetable = new Timetable(LocalDate.now().minusDays(1L), LocalDate.now(), new Stretch());
		final Timetable overlappingTimetable = new Timetable(LocalDate.now().minusDays(2L),
				LocalDate.now().minusDays(1L), new Stretch());
		assertThat(timetable.overlaps(overlappingTimetable), is(true));
	}

	@Test
	public void overlapsWithOverlappingStartAndEndDateTimetable() {
		final Timetable timetable = new Timetable(LocalDate.now().minusDays(1L), LocalDate.now(), new Stretch());
		final Timetable overlappingTimetable = new Timetable(LocalDate.now().minusDays(1L), LocalDate.now(),
				new Stretch());
		assertThat(timetable.overlaps(overlappingTimetable), is(true));
	}
}
