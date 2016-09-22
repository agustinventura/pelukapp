package com.spanishcoders.model;

import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Timetable {

    @Id
    @GeneratedValue
    protected Integer id;

    @NotNull
    private LocalDate startDay;

    @NotNull
    private LocalDate endDay;

    @ManyToOne
    private Agenda agendas;

    @NotEmpty
    @ManyToMany(mappedBy = "timetables", cascade = CascadeType.ALL)
    private Set<Stretch> stretches;

    public Timetable() {
        this.stretches = Sets.newHashSet();
        this.agendas = null;
    }

    public Timetable(LocalDate startDay, LocalDate endDay) {
        this();
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public Timetable(Agenda agenda, LocalDate startDay, LocalDate endDay) {
        this(startDay, endDay);
        this.agendas = agenda;
        agenda.addTimetable(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getStartDay() {
        return startDay;
    }

    public void setStartDay(LocalDate startDay) {
        this.startDay = startDay;
    }

    public LocalDate getEndDay() {
        return endDay;
    }

    public void setEndDay(LocalDate endDay) {
        this.endDay = endDay;
    }

    public Agenda getAgendas() {
        return agendas;
    }

    public void setAgendas(Agenda agendas) {
        this.agendas = agendas;
    }

    public Set<Stretch> getStretches() {
        return stretches;
    }

    public void setStretches(Set<Stretch> stretches) {
        this.stretches = stretches;
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "id=" + id +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                ", stretches=" + stretches +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timetable timetable = (Timetable) o;

        return id != null ? id.equals(timetable.id) : timetable.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void addStretch(Stretch stretch) {
        this.stretches.add(stretch);
    }
}
