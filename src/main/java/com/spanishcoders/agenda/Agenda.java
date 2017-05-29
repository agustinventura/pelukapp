package com.spanishcoders.agenda;

import com.google.common.collect.Sets;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Agenda {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @OneToOne
    private Hairdresser hairdresser;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL)
    @MapKey(name = "date")
    @OrderBy("date")
    private SortedMap<LocalDate, WorkingDay> workingDays;

    @ElementCollection
    private Set<LocalDate> nonWorkingDays;

    @NotEmpty
    @OneToMany(mappedBy = "agendas", cascade = CascadeType.ALL)
    private Set<Timetable> timetables;

    public Agenda() {
        this.workingDays = new TreeMap<>();
        this.timetables = Sets.newHashSet();
        this.nonWorkingDays = Sets.newHashSet();
    }

    public Agenda(Hairdresser hairdresser) {
        this();
        this.hairdresser = hairdresser;
        hairdresser.setAgenda(this);
    }

    public Agenda(Hairdresser hairdresser, Timetable timetable) {
        this(hairdresser);
        this.timetables.add(timetable);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Hairdresser getHairdresser() {
        return hairdresser;
    }

    public void setHairdresser(Hairdresser hairdresser) {
        this.hairdresser = hairdresser;
    }

    public SortedMap<LocalDate, WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(SortedMap<LocalDate, WorkingDay> workingDays) {
        this.workingDays = workingDays;
    }

    public Set<LocalDate> getNonWorkingDays() {
        return nonWorkingDays;
    }

    public void setNonWorkingDays(Set<LocalDate> nonWorkingDays) {
        this.nonWorkingDays = nonWorkingDays;
    }

    public Set<Timetable> getTimetables() {
        return timetables;
    }

    public void setTimetables(Set<Timetable> timetables) {
        this.timetables = timetables;
    }

    @Override
    public String toString() {
        return "Agenda{" +
                "id=" + id +
                ", hairdresser=" + hairdresser +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agenda agenda = (Agenda) o;

        if (id != null ? !id.equals(agenda.id) : agenda.id != null) return false;
        return hairdresser != null ? hairdresser.equals(agenda.hairdresser) : agenda.hairdresser == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (hairdresser != null ? hairdresser.hashCode() : 0);
        return result;
    }

    public void addWorkingDay(WorkingDay workingDay) {
        if (workingDay == null || workingDay.getDate() == null) {
            throw new IllegalArgumentException("To add a working day it needs to have a date");
        }
        workingDays.put(workingDay.getDate(), workingDay);
    }

    public void addTimetable(Timetable timetable) {
        if (timetable == null) {
            throw new IllegalArgumentException("Can't add an empty timetable to agenda");
        }
        this.timetables.add(timetable);
    }

    public Timetable getCurrentTimetable() {
        boolean found = false;
        LocalDate today = LocalDate.now();
        Timetable currentTimetable = null;
        Iterator<Timetable> timetablesIt = timetables.iterator();
        while (!found && timetablesIt.hasNext()) {
            currentTimetable = timetablesIt.next();
            if (currentTimetable.getStartDay().isBefore(today) && currentTimetable.getEndDay().isAfter(today)) {
                found = true;
            }
        }
        if (!found) {
            throw new TimeTableNotFoundException("Couldn't find an active timetable for date " + today);
        }
        return currentTimetable;
    }

    public void addNonWorkingDay(LocalDate nonWorkingDay) {
        this.nonWorkingDays.add(nonWorkingDay);
    }

    public boolean isNonWorkingDay(LocalDate today) {
        return nonWorkingDays.contains(today);
    }

    public boolean hasWorkingDay(LocalDate today) {
        return workingDays.containsKey(today);
    }

    public Set<Block> getWorkingDayBlocks(LocalDate day) {
        Set<Block> blocks = Sets.newHashSet();
        if (workingDays.containsKey(day)) {
            blocks = workingDays.get(day).getBlocks();
        }
        return blocks;
    }
}
