package com.spanishcoders.model;

import javax.persistence.*;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Work implements Comparable<Work> {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    /**
     * in minutes
     */
    private int duration;

    @Enumerated(EnumType.STRING)
    private WorkKind kind;

    public Work() {
    }

    public Work(String name, int duration, WorkKind kind) {
        this.name = name;
        this.duration = duration;
        this.kind = kind;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public WorkKind getKind() {
        return kind;
    }

    public void setKind(WorkKind kind) {
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Work work = (Work) o;

        if (id != null ? !id.equals(work.id) : work.id != null) return false;
        return name != null ? name.equals(work.name) : work.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Work o) {
        return this.getId().compareTo(o.getId());
    }
}
