package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.services.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by agustin on 21/06/16.
 */
@RestController
@RequestMapping(value = "/appointment", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentController {

    private AppointmentService appointmentService;


    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO confirmAppointment(Authentication authentication, @RequestBody AppointmentDTO appointment) {
        Appointment confirmed = appointmentService.confirmAppointment(authentication, appointment);
        return new AppointmentDTO(confirmed);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "{appointmentId}/cancel", method = RequestMethod.POST)
    public Appointment cancelAppointment(Authentication authentication, @PathVariable Integer appointmentId) {
        Optional<Appointment> maybeAppointment = appointmentService.get(appointmentId);
        Appointment cancelled = null;
        if (maybeAppointment.isPresent()) {
            cancelled = appointmentService.cancelAppointment(authentication, maybeAppointment.get());
            //TODO: This is a workaround for a lazy loading exception, we need to find a better solution
            if (cancelled.getUser() instanceof Hairdresser) {
                ((Hairdresser) cancelled.getUser()).setAgenda(null);
            }
        } else {
            throw new IllegalArgumentException("There's no Appointment with id " + appointmentId);
        }
        return cancelled;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedExceptionExceptionHandler() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
