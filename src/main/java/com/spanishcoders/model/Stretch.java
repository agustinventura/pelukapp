package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Stretch {

    @Id
    @GeneratedValue
    protected Integer id;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @ManyToOne
    private Timetable timetables;

    public Stretch() {
        this.timetables = null;
    }

    public Stretch(Timetable timetable, LocalTime startTime, LocalTime endTime) {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
        timetables = timetable;
        timetable.addStretch(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Timetable getTimetables() {
        return timetables;
    }

    public void setTimetables(Timetable timetables) {
        this.timetables = timetables;
    }

    @Override
    public String toString() {
        return "Stretch{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stretch stretch = (Stretch) o;

        if (id != null ? !id.equals(stretch.id) : stretch.id != null) return false;
        if (startTime != null ? !startTime.equals(stretch.startTime) : stretch.startTime != null) return false;
        return endTime != null ? endTime.equals(stretch.endTime) : stretch.endTime == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }
}
