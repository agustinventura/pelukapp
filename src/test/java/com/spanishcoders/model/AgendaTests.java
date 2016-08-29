package com.spanishcoders.model;

import org.junit.Test;

import java.time.LocalDate;

import static com.spanishcoders.TestDataFactory.mockTimetable;
import static com.spanishcoders.TestDataFactory.mockWorkingDay;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by agustin on 4/07/16.
 */
public class AgendaTests {

    @Test(expected = IllegalArgumentException.class)
    public void addNullWorkingDay() throws Exception {
        Agenda agenda = new Agenda();
        agenda.addWorkingDay(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullDateWorkingDay() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        workingDay.setDate(null);
        Agenda agenda = new Agenda();
        agenda.addWorkingDay(workingDay);
    }

    @Test
    public void addWorkingDay() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        Agenda agenda = new Agenda();
        agenda.addWorkingDay(workingDay);
        assertThat(agenda.getWorkingDays().size(), is(1));
        assertThat(agenda.getWorkingDays().values(), contains(workingDay));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullTimetable() throws Exception {
        Agenda agenda = new Agenda();
        agenda.addTimetable(null);
    }

    @Test
    public void addTimetable() throws Exception {
        Timetable timetable = mockTimetable();
        Agenda agenda = new Agenda();
        agenda.addTimetable(timetable);
        assertThat(agenda.getTimetables().size(), is(1));
        assertThat(agenda.getTimetables(), contains(timetable));
    }

    @Test
    public void getCurrentTimetable() throws Exception {
        Agenda agenda = new Agenda();
        LocalDate aMonthAgo = LocalDate.now().minusMonths(1);
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        Timetable current = new Timetable(agenda, aMonthAgo, nextMonth);
        LocalDate pastYearStartDate = aMonthAgo.minusYears(1);
        LocalDate pastYearEndDate = nextMonth.plusYears(1);
        Timetable past = new Timetable(agenda, pastYearStartDate, pastYearEndDate);
        Timetable currentTimetable = agenda.getCurrentTimetable();
        assertThat(currentTimetable, is(current));
    }
}