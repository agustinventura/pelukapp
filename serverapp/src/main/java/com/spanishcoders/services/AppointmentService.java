package com.spanishcoders.services;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.User;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.AppointmentRepository;
import com.spanishcoders.repositories.UserRepository;
import io.jsonwebtoken.lang.Collections;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by agustin on 7/07/16.
 */
@Service
@Transactional
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    private BlockService blockService;

    private UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, BlockService blockService, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.blockService = blockService;
        this.userRepository = userRepository;
    }

    public Appointment confirmAppointment(Authentication authentication, Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        Appointment confirmed = null;
        Set<Block> blocks = refreshBlocks(requestedBlocks);
        User requestUser = authentication != null ? userRepository.findByUsername(authentication.getName()) : null;
        confirmed = new Appointment(requestUser, requestedWorks, blocks);
        confirmed = appointmentRepository.save(confirmed);
        return confirmed;
    }

    private Set<Block> refreshBlocks(Set<Block> requestedBlocks) {
        if (requestedBlocks != null && !requestedBlocks.isEmpty()) {
            int[] blocksIds = requestedBlocks.stream().mapToInt(block -> block.getId()).toArray();
            requestedBlocks = blockService.get(Collections.arrayToList(blocksIds));
        }
        return requestedBlocks;
    }
}
