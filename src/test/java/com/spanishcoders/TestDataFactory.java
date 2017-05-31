package com.spanishcoders;

import static org.mockito.Mockito.mock;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.Stretch;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

/**
 * Created by agustin on 28/06/16.
 */
public class TestDataFactory {

	public static Set<Block> mockBlocks() {
		final WorkingDay mockWorkingDay = mockWorkingDay();
		return mockWorkingDay.getBlocks();
	}

	public static WorkingDay mockWorkingDay() {
		final Agenda mockAgenda = mockFullAgenda();
		return new WorkingDay(mockAgenda);
	}

	public static Set<Work> mockPrivateWorks() {
		final Work regulation = new Work("Regulacion", Duration.ofMinutes(30), WorkKind.PRIVATE);
		return Sets.newHashSet(regulation);
	}

	public static Set<Work> mockPublicWorks() {
		final Work cut = new Work("Corte", Duration.ofMinutes(30), WorkKind.PUBLIC);
		final Work shave = new Work("Afeitado", Duration.ofMinutes(30), WorkKind.PUBLIC);
		return Sets.newHashSet(cut, shave);
	}

	public static Set<Work> mockPublicWork() {
		final Work cut = new Work("Corte", Duration.ofMinutes(30), WorkKind.PUBLIC);
		return Sets.newHashSet(cut);
	}

	public static Set<Work> mockAllWorks() {
		return Sets.union(mockPublicWorks(), mockPrivateWorks());
	}

	public static Map<Hairdresser, Set<Block>> mockBlocksByHairdresser() {
		final Map<Hairdresser, Set<Block>> availableBlocksByHairDresser = Maps.newHashMap();
		availableBlocksByHairDresser.put(mockHairdresser(), mockTenBlocks());
		return availableBlocksByHairDresser;
	}

	public static Hairdresser mockHairdresser() {
		return new Hairdresser("admin", "admin", "phone");
	}

	public static Hairdresser mockHairdresser(Agenda agenda) {
		final Hairdresser hairdresser = mockHairdresser();
		hairdresser.setAgenda(agenda);
		return hairdresser;
	}

	public static Agenda mockFullAgenda() {
		final Agenda agenda = new Agenda(mockHairdresser());
		final LocalDate today = LocalDate.now();
		final Timetable timetable = new Timetable(agenda, today.minusDays(1), today.plusDays(1));
		final Stretch morning = new Stretch(timetable, LocalTime.of(9, 30), LocalTime.of(14, 00));
		final Stretch afternoon = new Stretch(timetable, LocalTime.of(17, 00), LocalTime.of(20, 30));
		return agenda;
	}

	public static Timetable mockTimetable() {
		final LocalDate today = LocalDate.now();
		final Timetable timetable = new Timetable(mock(Agenda.class), today.minusDays(1), today.plusDays(1));
		return timetable;
	}

	public static Set<Stretch> mockStretches() {
		final Timetable timetable = mockTimetable();
		final Stretch morning = new Stretch(timetable, LocalTime.of(9, 30), LocalTime.of(14, 00));
		final Stretch afternoon = new Stretch(timetable, LocalTime.of(17, 00), LocalTime.of(20, 30));
		return Sets.newHashSet(morning, afternoon);
	}

	public static Set<Block> mockTenBlocks() {
		final Set<Block> blocks = Sets.newHashSet();
		LocalTime now = LocalTime.now();
		final WorkingDay workingDay = new WorkingDay();
		workingDay.setId(1);
		workingDay.setDate(LocalDate.now());
		for (int i = 0; i < 10; i++) {
			blocks.add(new Block(now, workingDay));
			now = now.plusMinutes(30);
		}
		return blocks;
	}

	public static WorkingDay mockRandomFillWorkingDay() {
		final WorkingDay workingDay = mockWorkingDay();
		final SecureRandom secureRandom = new SecureRandom();
		for (final Block block : workingDay.getBlocks()) {
			if (secureRandom.nextInt() % 2 == 0) {
				block.setAppointment(new Appointment());
			}
		}
		return workingDay;
	}
}
