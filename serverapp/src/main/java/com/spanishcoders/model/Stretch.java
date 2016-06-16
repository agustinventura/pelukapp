package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Stretch {

    @Id
    @GeneratedValue
    protected Integer id;

    private LocalTime start;

    private Duration length;

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

    public Duration getLength() {
        return length;
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
}
