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

    @NotNull
    @ManyToOne
    private Hairdresser hairdresser;

    @ManyToOne
    private Client client;

    /**
     * in minutes
     */
    private int duration;

    private String notes;

    @ManyToMany
    private Set<Service> bookedServices;

    public int getId() {
        return id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Hairdresser getHairdresser() {
        return hairdresser;
    }

    public void setHairdresser(Hairdresser hairdresser) {
        this.hairdresser = hairdresser;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public Set<Service> getBookedServices() {
        return bookedServices;
    }

    public void setBookedServices(Set<Service> bookedServices) {
        this.bookedServices = bookedServices;
    }
}
