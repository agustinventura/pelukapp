package com.spanishcoders.model.dto;

import com.spanishcoders.model.Hairdresser;

import java.util.TreeSet;

/**
 * Created by agustin on 8/08/16.
 */
public class HairdresserDTO extends UserDTO {

    private Integer agenda;

    public HairdresserDTO(Hairdresser hairdresser) {
        this.setId(hairdresser.getId());
        this.setName(hairdresser.getName());
        this.setUsername(hairdresser.getUsername());
        this.setPassword(hairdresser.getPassword());
        this.setPhone(hairdresser.getPhone());
        this.setStatus(hairdresser.getStatus().ordinal());
        this.setAgenda(hairdresser.getAgenda().getId());
        this.setAppointments(new TreeSet<>());
        hairdresser.getAppointments().stream().forEach(appointment -> this.getAppointments().add(appointment.getId()));
    }

    public Integer getAgenda() {
        return agenda;
    }

    public void setAgenda(Integer agenda) {
        this.agenda = agenda;
    }
}
