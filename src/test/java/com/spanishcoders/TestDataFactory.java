package com.spanishcoders;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.google.common.collect.Sets;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.OpeningHours;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkStatus;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

public class TestDataFactory {

	public static Set<Block> blocks() {
		final WorkingDay mockWorkingDay = workingDay();
		return mockWorkingDay.getBlocks();
	}

	public static WorkingDay workingDay() {
		final Agenda mockAgenda = agenda();
		mockAgenda.addTimetable(timetable());
		mockAgenda.addWorkingDay(mockAgenda.getCurrentTimetable().getStartDate());
		return mockAgenda.getWorkingDays().get(mockAgenda.getCurrentTimetable().getStartDate());
	}

	public static Set<Work> privateWorks() {
		final Work regulation = new Work("Regulacion", Duration.ofMinutes(30), WorkKind.PRIVATE, WorkStatus.ENABLED);
		return Sets.newHashSet(regulation);
	}

	public static Set<Work> publicWorks() {
		final Work cut = new Work("Corte", Duration.ofMinutes(30), WorkKind.PUBLIC, WorkStatus.ENABLED);
		final Work shave = new Work("Afeitado", Duration.ofMinutes(30), WorkKind.PUBLIC, WorkStatus.ENABLED);
		return Sets.newHashSet(cut, shave);
	}

	public static Set<Work> publicWork() {
		final Work cut = new Work("Corte", Duration.ofMinutes(30), WorkKind.PUBLIC, WorkStatus.ENABLED);
		return Sets.newHashSet(cut);
	}

	public static Set<Work> allWorks() {
		return Sets.union(publicWorks(), privateWorks());
	}

	public static Hairdresser hairdresser() {
		return new Hairdresser("admin", "admin", "phone");
	}

	public static Agenda agenda() {
		final Agenda agenda = new Agenda(hairdresser());
		return agenda;
	}

	public static Timetable timetable() {
		final LocalDate startDay = LocalDate.now();
		final LocalDate endDay = LocalDate.now().plusMonths(6);
		final Timetable timetable = new Timetable(startDay, endDay);
		timetable.addOpeningDay(startDay.getDayOfWeek(),
				openingHours().toArray(new OpeningHours[openingHours().size()]));
		return timetable;
	}

	public static Set<OpeningHours> openingHours() {
		final OpeningHours morning = new OpeningHours(LocalTime.of(9, 30), LocalTime.of(14, 00));
		final OpeningHours afternoon = new OpeningHours(LocalTime.of(17, 00), LocalTime.of(20, 30));
		return Sets.newHashSet(morning, afternoon);
	}
}
