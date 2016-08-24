package com.spanishcoders.model;

import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull
    private LocalTime start;

    @NotNull
    @Column(name = "`end`")
    private LocalTime end;

    @NotEmpty
    @ManyToMany
    private Set<Timetable> timetables;

    public Stretch() {
        this.timetables = Sets.newHashSet();
    }

    public Stretch(Timetable timetable, LocalTime start, LocalTime end) {
        this();
        this.start = start;
        this.end = end;
        timetables.add(timetable);
        timetable.addStretch(this);
    }

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

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
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
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stretch stretch = (Stretch) o;

        if (id != null ? !id.equals(stretch.id) : stretch.id != null) return false;
        if (start != null ? !start.equals(stretch.start) : stretch.start != null) return false;
        return end != null ? end.equals(stretch.end) : stretch.end == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
