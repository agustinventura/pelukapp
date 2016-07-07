package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.services.AppointmentService;
import com.spanishcoders.services.BlockService;
import com.spanishcoders.services.WorkService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "new/{works}&{blocks}", method = RequestMethod.GET)
    public Appointment getFreeBlocks(Authentication authentication, @MatrixVariable Set<Integer> works, @MatrixVariable Set<Integer> blocks) {
        Set<Work> requestedWorks = workService.get(works);
        Set<Block> requestedBlocks = blockService.get(blocks);
        Appointment confirmed = appointmentService.confirmAppointment(requestedWorks, requestedBlocks);
        return confirmed;
    }
}
