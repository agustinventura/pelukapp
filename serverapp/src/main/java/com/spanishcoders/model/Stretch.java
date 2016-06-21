package com.spanishcoders.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Stretch {

    @Id
    @GeneratedValue
    protected Integer id;

    @NotNull
    private LocalTime start;

    private Duration length;

    @NotEmpty
    @ManyToMany
    private Set<Timetable> timetables;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public Optional<Duration> getLength() {
        if (length != null) {
            return Optional.of(length);
        } else {
            return Optional.empty();
        }
    }

    public void setLength(Duration length) {
        this.length = length;
    }

    public Set<Timetable> getTimetables() {
        return timetables;
    }

    public void setTimetables(Set<Timetable> timetables) {
        this.timetables = timetables;
    }

    @Override
    public String toString() {
        return "Stretch{" +
                "start=" + start +
                ", length=" + length +
                '}';
    }
}
