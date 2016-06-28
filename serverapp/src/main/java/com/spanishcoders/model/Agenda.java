package com.spanishcoders.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @OneToOne
    private Hairdresser hairdresser;

    @NotEmpty
    @OneToMany(mappedBy = "agenda")
    private Set<WorkingDay> workingDays;

    @NotEmpty
    @ElementCollection
    private Set<LocalDate> nonWorkingDays;

    @NotEmpty
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

    @Override
    public String toString() {
        return "Agenda{" +
                "hairdresser=" + hairdresser +
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
}
