package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Hairdresser extends User {
    @OneToMany(mappedBy = "hairdresser")
    @OrderBy(value = "startTime asc")
    private Set<Appointment> appointments;

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}
