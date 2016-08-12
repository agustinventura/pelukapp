package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;
import com.spanishcoders.model.User;

import java.util.Set;

/**
 * Created by pep on 04/08/2016.
 */
public class UserDTO {

    private Integer id;
    private String name;
    private String username;
    private String password;
    private String phone;
    private Integer status;
    private Set<Integer> appointments;

    public UserDTO() {
        appointments = Sets.newTreeSet();
    }

    public UserDTO(User user) {
        this();
        this.setId(user.getId());
        this.setName(user.getName());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setPhone(user.getPhone());
        this.setStatus(user.getStatus().ordinal());
        user.getAppointments().stream().forEach(appointment -> this.getAppointments().add(appointment.getId()));
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<Integer> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Integer> appointments) {
        this.appointments = appointments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (id != null ? !id.equals(userDTO.id) : userDTO.id != null) return false;
        return username != null ? username.equals(userDTO.username) : userDTO.username == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", appointments=" + appointments +
                '}';
    }
}
