package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
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
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO createAppointment(Authentication authentication, @RequestBody AppointmentDTO appointment) {
        Appointment confirmed = appointmentService.confirmAppointment(authentication, appointment);
        return new AppointmentDTO(confirmed);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(method = RequestMethod.PUT)
    public AppointmentDTO updateAppointment(Authentication authentication, @PathVariable Integer appointmentId) {
        Optional<Appointment> maybeAppointment = appointmentService.get(appointmentId);
        AppointmentDTO cancelled = null;
        if (maybeAppointment.isPresent()) {
            cancelled = new AppointmentDTO(appointmentService.cancelAppointment(authentication, maybeAppointment.get()));
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
