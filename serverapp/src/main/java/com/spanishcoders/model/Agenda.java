package com.spanishcoders.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Agenda {

    @Id
    @GeneratedValue
    protected Integer id;

    @OneToOne
    private Hairdresser hairdresser;

    @OneToMany(mappedBy = "agenda")
    private Set<WorkingDay> workingDays;

    @ElementCollection
    private Set<LocalDate> nonWorkingDays;

    @ManyToMany(mappedBy = "agendas")
    private Set<Timetable> timetables;

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

    public Set<WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Set<WorkingDay> workingDays) {
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
}
