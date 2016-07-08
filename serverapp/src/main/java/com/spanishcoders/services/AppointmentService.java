package com.spanishcoders.services;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Set;

/**
 * Created by agustin on 7/07/16.
 */
@Service
public class AppointmentService {
    public Appointment confirmAppointment(Principal user, Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        Appointment confirmed = null;
        return confirmed;
    }
}
