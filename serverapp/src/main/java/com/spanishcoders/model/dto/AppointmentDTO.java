package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Appointment;

import java.util.Set;

/**
 * Created by agustin on 11/08/16.
 */
public class AppointmentDTO {

    private Integer id;
    private Set<Integer> blocks;
    private Set<Integer> works;
    private Integer user;
    private String date;
    private String duration;
    private Integer status;

    public AppointmentDTO() {
        blocks = Sets.newTreeSet();
        works = Sets.newTreeSet();
    }

    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.user = appointment.getUser() != null ? appointment.getUser().getId() : null;
        this.date = appointment.getDate() != null ? appointment.getDate().toString() : null;
        this.duration = appointment.getDuration() != null ? appointment.getDuration().toString() : null;
        this.status = appointment.getStatus() != null ? appointment.getStatus().ordinal() : null;
        if (appointment.getBlocks() != null) {
            appointment.getBlocks().stream().forEach(block -> this.blocks.add(block.getId()));
        }
        if (appointment.getWorks() != null) {
            appointment.getWorks().stream().forEach(work -> this.works.add(work.getId()));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Integer> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Integer> blocks) {
        this.blocks = blocks;
    }

    public Set<Integer> getWorks() {
        return works;
    }

    public void setWorks(Set<Integer> works) {
        this.works = works;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentDTO that = (AppointmentDTO) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AppointmentDTO{" +
                "id=" + id +
                ", blocks=" + blocks +
                ", works=" + works +
                ", user=" + user +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", status=" + status +
                '}';
    }
}
