package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */

@Entity
public class Client implements User {

    @Id
    private int id;

    @NotNull
    private String name;

    private String password;

    @NotNull
    private String phone;

    private String distance;

    @OneToMany(mappedBy = "client")
    @OrderBy(value = "startTime asc" )
    private Set<Appointment> appointments;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}
