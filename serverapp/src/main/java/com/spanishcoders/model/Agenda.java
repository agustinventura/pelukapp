package com.spanishcoders.model;

import com.google.common.collect.Sets;
import com.spanishcoders.model.exceptions.TimeTableNotFoundException;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Agenda {

    @Id
    @GeneratedValue
    protected Integer id;

    @NotNull
    @OneToOne
    private Hairdresser hairdresser;

    @NotEmpty
    @OneToMany(mappedBy = "agenda")
    @MapKey(name = "date")
    @OrderBy("date")
    private SortedMap<LocalDate, WorkingDay> workingDays;

    @ElementCollection
    private Set<LocalDate> nonWorkingDays;

    @NotEmpty
    @ManyToMany(mappedBy = "agendas")
    private Set<Timetable> timetables;

    public Agenda() {
        this.workingDays = new TreeMap<>();
        this.timetables = Sets.newHashSet();
        this.nonWorkingDays = Sets.newHashSet();
    }

    public Agenda(Hairdresser hairdresser) {
        this();
        this.hairdresser = hairdresser;
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
                "hairdresser=" + hairdresser.getId() +
                ", workingDays=" + workingDays +
                ", nonWorkingDays=" + nonWorkingDays +
                ", timetables=" + timetables +
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
        workingDays.put(workingDay.getDate(), workingDay);
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

    public Set<Block> getFirstTenAvailableBlocks(Set<Work> works) {
        Set<Block> availableBlocks = Sets.newHashSet();
        if (works != null && !works.isEmpty()) {
            while (availableBlocks.size() < 10) {
                for (Map.Entry<LocalDate, WorkingDay> entry : getWorkingDays().entrySet()) {
                    availableBlocks.addAll(entry.getValue().getAvailableBlocks(works));
                }
            }
        }
        return availableBlocks;
    }

    public void addTimetable(Timetable timetable) {
        this.timetables.add(timetable);
    }
}
