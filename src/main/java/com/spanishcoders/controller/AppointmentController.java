package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.services.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by agustin on 21/06/16.
 */
@RestController
@RequestMapping(value = "/appointment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

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
            logger.error("User " + authentication.getName() + " tried to cancel non-existing appointment " + appointment);
            throw new IllegalArgumentException("There's no Appointment which matches " + appointment);
        }
        return new AppointmentDTO(cancelled);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler(HttpServletRequest req, Exception ex) {
        logger.error("Caught IllegalArgumentException processing Appointment request " + req.getRequestURL() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedExceptionExceptionHandler(HttpServletRequest req, Exception ex) {
        logger.error("Caught AccessDeniedException processing Appointment request " + req.getRequestURL() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
