package com.spanishcoders.services;

import com.spanishcoders.model.*;
import com.spanishcoders.repositories.AppointmentRepository;
import com.spanishcoders.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;

/**
 * Created by agustin on 7/07/16.
 */
@Service
@Transactional
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    private UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Appointment confirmAppointment(Authentication authentication, Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        Appointment confirmed = null;
        checkAuthentication(authentication);
        checkWorks(requestedWorks);
        checkBlocks(requestedBlocks);
        checkAuthorization(authentication.getAuthorities(), requestedWorks);
        checkWorkLength(requestedWorks, requestedBlocks);
        User requestUser = userRepository.findByUsername(authentication.getName());
        confirmed = new Appointment(requestUser, requestedWorks, requestedBlocks);
        confirmed = appointmentRepository.save(confirmed);
        return confirmed;
    }

    private void checkWorkLength(Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        int worksLength = requestedWorks.stream().mapToInt(work -> work.getDuration()).sum();
        long blocksLength = requestedBlocks.stream().mapToLong(block -> block.getLength().toMinutes()).sum();
        if (blocksLength < worksLength) {
            throw new IllegalArgumentException("Blocks length equal or greater than works length");
        }
    }

    private void checkAuthorization(Collection<? extends GrantedAuthority> authorities, Set<Work> requestedWorks) {
        if (requestedWorks.stream().anyMatch(work -> work.getKind() == WorkKind.PRIVATE)) {
            if (!authorities.stream().anyMatch(authority -> authority.equals(Role.WORKER.getGrantedAuthority()))) {
                throw new AccessDeniedException("A client cannot appoint private works");
            }
        }
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
