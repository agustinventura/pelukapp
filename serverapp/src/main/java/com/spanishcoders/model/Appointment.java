package com.spanishcoders.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */

@Entity
public class Appointment {

    @Id
    private int id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @ManyToOne
    private Client client;

    @NotNull
    @ManyToOne
    private Hairdresser hairdresser;

    /**
     * in minutes
     */
    private int duration;

    private String notes;

    @ManyToMany
    private Set<Work> bookedWorks;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public int getId() {
        return id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Hairdresser getHairdresser() {
        return hairdresser;
    }

    public void setHairdresser(Hairdresser hairdresser) {
        this.hairdresser = hairdresser;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Work> getBookedWorks() {
        return bookedWorks;
    }

    public void setBookedWorks(Set<Work> bookedWorks) {
        this.bookedWorks = bookedWorks;
    }
}
