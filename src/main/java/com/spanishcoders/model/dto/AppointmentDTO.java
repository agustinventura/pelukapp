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

    private final Integer id;
    private final Set<Integer> blocks;
    private final Set<Integer> works;
    private final Integer user;
    private final String date;
    private final String duration;
    private final Integer status;
    private final String notes;

    public AppointmentDTO() {
        id = 0;
        user = 0;
        date = "";
        duration = "";
        status = 1;
        notes = "";
        blocks = Sets.newTreeSet();
        works = Sets.newTreeSet();
    }

    public AppointmentDTO(Appointment appointment) {
        id = appointment.getId();
        user = appointment.getUser() != null ? appointment.getUser().getId() : null;
        status = appointment.getStatus() != null ? appointment.getStatus().ordinal() : null;
        blocks = Sets.newTreeSet();
        if (appointment.getBlocks() != null && !appointment.getBlocks().isEmpty()) {
            blocks.addAll(appointment.getBlocks().stream().map(block -> block.getId()).collect(Collectors.toSet()));
            date = appointment.getBlocks().stream().findFirst().get().getWorkingDay().getDate().toString();
        } else {
            date = "";
        }
        works = Sets.newTreeSet();
        if (appointment.getWorks() != null && !appointment.getWorks().isEmpty()) {
            works.addAll(appointment.getWorks().stream().map(work -> work.getId()).collect(Collectors.toSet()));
            duration = Duration.of(appointment.getWorks().stream().mapToInt(work -> work.getDuration()).sum(), ChronoUnit.MINUTES).toString();
        } else {
            duration = "";
        }
        notes = appointment.getNotes();
    }

    public Integer getId() {
        return id;
    }

    public Set<Integer> getBlocks() {
        return blocks;
    }

    public Set<Integer> getWorks() {
        return works;
    }

    public Integer getUser() {
        return user;
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

    public String getNotes() {
        return notes;
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
                ", notes=" + notes +
                '}';
    }
}
