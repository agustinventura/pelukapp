package com.spanishcoders;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spanishcoders.model.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;

/**
 * Created by agustin on 28/06/16.
 */
public class TestDataFactory {

    public static Set<Block> mockBlocks() {
        WorkingDay mockWorkingDay = mockWorkingDay();
        return mockWorkingDay.getBlocks();
    }

    public static WorkingDay mockWorkingDay() {
        Agenda mockAgenda = mockFullAgenda();
        return new WorkingDay(mockAgenda);
    }

    public static Set<Work> mockPrivateWorks() {
        Work regulation = new Work("Regulacion", 30, WorkKind.PRIVATE);
        return Sets.newHashSet(regulation);
    }

    public static Set<Work> mockPublicWorks() {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        Work shave = new Work("Afeitado", 30, WorkKind.PUBLIC);
        return Sets.newHashSet(cut, shave);
    }

    public static Set<Work> mockPublicWork() {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        return Sets.newHashSet(cut);
    }

    public static Set<Work> mockAllWorks() {
        return Sets.union(mockPublicWorks(), mockPrivateWorks());
    }

    public static Map<Hairdresser, Set<Block>> mockBlocksByHairdresser() {
        Map<Hairdresser, Set<Block>> availableBlocksByHairDresser = Maps.newHashMap();
        availableBlocksByHairDresser.put(mockHairdresser(), mockTenBlocks());
        return availableBlocksByHairDresser;
    }

    public static Hairdresser mockHairdresser() {
        return new Hairdresser("admin", "admin", "phone");
    }

    public static Hairdresser mockHairdresser(Agenda agenda) {
        Hairdresser hairdresser = mockHairdresser();
        hairdresser.setAgenda(agenda);
        return hairdresser;
    }

    public static Agenda mockFullAgenda() {
        Agenda agenda = new Agenda(mockHairdresser());
        LocalDate today = LocalDate.now();
        Timetable timetable = new Timetable(agenda, today.minusDays(1), today.plusDays(1));
        Stretch morning = new Stretch(timetable, LocalTime.of(9, 30), LocalTime.of(14, 00));
        Stretch afternoon = new Stretch(timetable, LocalTime.of(17, 00), LocalTime.of(20, 30));
        return agenda;
    }

    public static Timetable mockTimetable() {
        LocalDate today = LocalDate.now();
        Timetable timetable = new Timetable(mock(Agenda.class), today.minusDays(1), today.plusDays(1));
        return timetable;
    }

    public static Set<Stretch> mockStretches() {
        Timetable timetable = mockTimetable();
        Stretch morning = new Stretch(timetable, LocalTime.of(9, 30), LocalTime.of(14, 00));
        Stretch afternoon = new Stretch(timetable, LocalTime.of(17, 00), LocalTime.of(20, 30));
        return Sets.newHashSet(morning, afternoon);
    }

    public static Set<Block> mockTenBlocks() {
        Set<Block> blocks = Sets.newHashSet();
        LocalTime now = LocalTime.now();
        WorkingDay workingDay = new WorkingDay();
        workingDay.setId(1);
        workingDay.setDate(LocalDate.now());
        for (int i = 0; i < 10; i++) {
            blocks.add(new Block(now, workingDay));
            now = now.plusMinutes(30);
        }
        return blocks;
    }

    public static WorkingDay mockRandomFillWorkingDay() {
        WorkingDay workingDay = mockWorkingDay();
        SecureRandom secureRandom = new SecureRandom();
        for (Block block : workingDay.getBlocks()) {
            if (secureRandom.nextInt() % 2 == 0) {
                block.setAppointment(new Appointment());
            }
        }
        return workingDay;
    }
}
