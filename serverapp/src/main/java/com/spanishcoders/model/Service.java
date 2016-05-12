package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Service {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    /**
     * in minutes
     */
    private int duration;

    public Service() {
    }

    public Service(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
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
}
