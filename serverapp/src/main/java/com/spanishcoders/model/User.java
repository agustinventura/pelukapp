package com.spanishcoders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.spanishcoders.model.security.UserDeserializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonDeserialize(using = UserDeserializer.class)
public class User {

    @Id
    @GeneratedValue
    protected Integer id;

    @NotNull
    protected String name;

    @NotNull
    protected String username;

    @NotNull
    protected String password;

    @NotNull
    protected String phone;

    @NotNull
    protected UserStatus status;

    @OneToMany(mappedBy = "user")
    @OrderBy(value = "date asc")
    @JsonIgnore
    private Set<Appointment> appointments;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public User() {
        this.status = UserStatus.ACTIVE;
        this.appointments = Sets.newTreeSet();
    }

    public User(String username, String password, String phone) {
        this();
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

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

    public Integer getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }
}
