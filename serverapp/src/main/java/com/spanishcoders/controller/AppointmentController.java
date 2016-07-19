package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.services.AppointmentService;
import com.spanishcoders.services.BlockService;
import com.spanishcoders.services.WorkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * Created by agustin on 21/06/16.
 */
@RestController
@RequestMapping(value = "/appointment", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentController {

    private AppointmentService appointmentService;

    private WorkService workService;

    private BlockService blockService;

    public AppointmentController(AppointmentService appointmentService, WorkService workService, BlockService blockService) {
        this.appointmentService = appointmentService;
        this.workService = workService;
        this.blockService = blockService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "new/{works}&{blocks}", method = RequestMethod.PUT)
    public Appointment confirmAppointment(Authentication authentication, @MatrixVariable Set<Integer> works, @MatrixVariable Set<Integer> blocks) {
        Set<Work> requestedWorks = workService.get(works);
        Set<Block> requestedBlocks = blockService.get(blocks);
        Appointment confirmed = appointmentService.confirmAppointment(authentication, requestedWorks, requestedBlocks);
        return confirmed;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "{appointmentId}/cancel", method = RequestMethod.POST)
    public Appointment cancelAppointment(Authentication authentication, @PathVariable Integer appointmentId) {
        Optional<Appointment> maybeAppointment = appointmentService.get(appointmentId);
        Appointment cancelled = null;
        if (maybeAppointment.isPresent()) {
            cancelled = appointmentService.cancelAppointment(authentication, maybeAppointment.get());
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
