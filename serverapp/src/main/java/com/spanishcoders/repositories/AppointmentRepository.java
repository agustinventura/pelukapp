package com.spanishcoders.repositories;

import com.spanishcoders.model.Appointment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by agustin on 8/07/16.
 */
public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {

}
