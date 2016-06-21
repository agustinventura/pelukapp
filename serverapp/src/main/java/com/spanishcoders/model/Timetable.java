package com.spanishcoders.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.Duration;
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

    private LocalDate startDay;

    private Duration validity;

    @NotEmpty
    @ManyToMany
    private Set<Agenda> agendas;

    @NotEmpty
    @ManyToMany(mappedBy = "timetables")
    private Set<Stretch> stretches;

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

    public Duration getValidity() {
        return validity;
    }

    public void setValidity(Duration validity) {
        this.validity = validity;
    }

    public Set<Agenda> getAgendas() {
        return agendas;
    }

    public void setAgendas(Set<Agenda> agendas) {
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
                "startDay=" + startDay +
                ", validity=" + validity +
                ", stretches=" + stretches +
                '}';
    }
}
