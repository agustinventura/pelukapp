package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Appointment;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

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
        this();
        this.id = appointment.getId();
        this.user = appointment.getUser() != null ? appointment.getUser().getId() : null;
        this.status = appointment.getStatus() != null ? appointment.getStatus().ordinal() : null;
        if (appointment.getBlocks() != null && !appointment.getBlocks().isEmpty()) {
            this.blocks.addAll(appointment.getBlocks().stream().map(block -> block.getId()).collect(Collectors.toSet()));
            this.date = appointment.getBlocks().stream().findFirst().get().getWorkingDay().getDate().toString();
        }
        if (appointment.getWorks() != null && !appointment.getWorks().isEmpty()) {
            this.works.addAll(appointment.getWorks().stream().map(work -> work.getId()).collect(Collectors.toSet()));
            this.duration = Duration.of(appointment.getWorks().stream().mapToInt(work -> work.getDuration()).sum(), ChronoUnit.MINUTES).toString();
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

    public String getDuration() {
        return duration;
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
