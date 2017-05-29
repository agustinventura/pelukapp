package com.spanishcoders.appointment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.spanishcoders.user.AppUser;

import java.util.SortedSet;

/**
 * Created by agustin on 8/07/16.
 */
public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {

    @Query("select a from Appointment a where a.user = :user and a.status = :status and a.date >= CURRENT_DATE")
    SortedSet<Appointment> getNextAppointments(@Param("user") AppUser user, @Param("status") AppointmentStatus status);
}
