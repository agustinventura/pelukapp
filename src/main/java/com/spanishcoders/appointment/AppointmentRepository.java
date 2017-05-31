package com.spanishcoders.appointment;

import java.util.SortedSet;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.spanishcoders.user.AppUser;

public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {

	@Query("select a from Appointment a where a.user = :user and a.status = :status and a.date >= CURRENT_DATE")
	SortedSet<Appointment> getNextAppointments(@Param("user") AppUser user, @Param("status") AppointmentStatus status);
}
