package com.spanishcoders.model.dto;

import com.spanishcoders.model.Hairdresser;

import java.util.TreeSet;

/**
 * Created by agustin on 8/08/16.
 */
public class HairdresserDTO extends UserDTO {

    private Integer agenda;

    public HairdresserDTO() {
    }

    public HairdresserDTO(Hairdresser hairdresser) {
        this.setId(hairdresser.getId());
        this.setName(hairdresser.getName());
        this.setUsername(hairdresser.getUsername());
        this.setPassword(hairdresser.getPassword());
        this.setPhone(hairdresser.getPhone());
        this.setStatus(hairdresser.getStatus().ordinal());
        this.setAgenda(hairdresser.getAgenda() != null ? hairdresser.getAgenda().getId() : null);
        this.setAppointments(new TreeSet<>());
        hairdresser.getAppointments().stream().forEach(appointment -> this.getAppointments().add(appointment.getId()));
    }

    public Integer getAgenda() {
        return agenda;
    }

    public void setAgenda(Integer agenda) {
        this.agenda = agenda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HairdresserDTO that = (HairdresserDTO) o;

        return agenda != null ? agenda.equals(that.agenda) : that.agenda == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (agenda != null ? agenda.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HairdresserDTO{" +
                "agenda=" + agenda +
                '}';
    }
}
