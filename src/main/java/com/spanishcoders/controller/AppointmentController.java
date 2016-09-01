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
@RequestMapping(value = "/appointment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentController {

    private AppointmentService appointmentService;


    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(method = RequestMethod.POST)
    public AppointmentDTO createAppointment(Authentication authentication, @RequestBody AppointmentDTO appointment) {
        Appointment confirmed = appointmentService.confirmAppointment(authentication, appointment);
        return new AppointmentDTO(confirmed);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(method = RequestMethod.PUT)
    public AppointmentDTO cancelAppointment(Authentication authentication, @RequestBody AppointmentDTO appointment) {
        Optional<Appointment> maybeAppointment = appointmentService.get(appointment.getId());
        Appointment cancelled = null;
        if (maybeAppointment.isPresent()) {
            cancelled = appointmentService.cancelAppointment(authentication, maybeAppointment.get());
        } else {
            throw new IllegalArgumentException("There's no Appointment which matches " + appointment);
        }
        return new AppointmentDTO(cancelled);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedExceptionExceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
