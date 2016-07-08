package com.spanishcoders.services;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.AppointmentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Created by agustin on 7/07/16.
 */
@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment confirmAppointment(Authentication authentication, Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        Appointment confirmed = null;
        checkAuthentication(authentication);
        checkWorks(requestedWorks);
        checkBlocks(requestedBlocks);
        return confirmed;
    }

    private void checkBlocks(Set<Block> requestedBlocks) {
        if (CollectionUtils.isEmpty(requestedBlocks)) {
            throw new IllegalArgumentException("Blocks are mandatory to create an Appointment");
        }
    }

    private void checkWorks(Set<Work> requestedWorks) {
        if (CollectionUtils.isEmpty(requestedWorks)) {
            throw new IllegalArgumentException("Works are mandatory to create an Appointment");
        }
    }

    private void checkAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication is mandatory to create an Appointment");
        }
    }
}
