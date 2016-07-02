package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Client extends User {

    private String distance;

    @OneToMany(mappedBy = "client")
    @OrderBy(value = "startTime asc")
    private Set<Appointment> appointments;

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Client{" +
                "username=" + username +
                "distance='" + distance + '\'' +
                ", appointments=" + appointments +
                '}';
    }
}
