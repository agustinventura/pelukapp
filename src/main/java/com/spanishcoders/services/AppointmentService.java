package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.*;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.repositories.AppointmentRepository;
import com.spanishcoders.repositories.UserRepository;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Created by agustin on 7/07/16.
 */
@Service
@Transactional
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    private BlockService blockService;

    private WorkService workService;

    private UserRepository userRepository;

    @Value("${max_hours_to_cancel_as_client:24}")
    private int maxHoursToCancelAsClient;

    public AppointmentService(AppointmentRepository appointmentRepository, BlockService blockService, WorkService workService, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.blockService = blockService;
        this.workService = workService;
        this.userRepository = userRepository;
    }

    public Appointment confirmAppointment(Authentication authentication, AppointmentDTO appointmentDTO) {
        Appointment confirmed = null;
        Set<Block> blocks = blockService.get(appointmentDTO.getBlocks());
        Set<Work> works = workService.get(appointmentDTO.getWorks());
        AppUser requestUser = authentication != null ? userRepository.findByUsername(authentication.getName()) : null;
        confirmed = new Appointment(requestUser, works, blocks);
        confirmed = appointmentRepository.save(confirmed);
        return confirmed;
    }

    public Appointment cancelAppointment(Authentication authentication, Appointment appointment) {
        checkUserRole(authentication, appointment);
        checkIfUserIsProprietaryOrAdmin(authentication, appointment);
        appointment.setBlocks(refreshBlocks(appointment.getBlocks()));
        appointment.cancel();
        return appointmentRepository.save(appointment);
    }

    private void checkIfUserIsProprietaryOrAdmin(Authentication authentication, Appointment appointment) {
        AppUser requestUser = authentication != null ? userRepository.findByUsername(authentication.getName()) : null;
        if (!requestUser.equals(appointment.getUser())) {
            Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
            if (!userAuthorities.stream().anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
                throw new AccessDeniedException("To cancel another AppUser Appointments, AppUser needs to be Worker");
            }
        }
    }

    private void checkUserRole(Authentication authentication, Appointment appointment) {
        if (appointment.getDate().isBefore(LocalDateTime.now().plusHours(maxHoursToCancelAsClient))) {
            Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
            if (!userAuthorities.stream().anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
                throw new AccessDeniedException("To cancel an Appointment in less than 24 hours, AppUser needs to be Worker");
            }
        }
    }

    private Set<Block> refreshBlocks(Set<Block> requestedBlocks) {
        if (requestedBlocks != null && !requestedBlocks.isEmpty()) {
            int[] blocksIds = requestedBlocks.stream().mapToInt(block -> block.getId()).toArray();
            requestedBlocks = blockService.get(Collections.arrayToList(blocksIds));
        }
        return requestedBlocks;
    }

    public Optional<Appointment> get(Integer appointmentId) {
        Optional<Appointment> appointment = Optional.empty();
        if (appointmentId != null) {
            appointment = Optional.ofNullable(appointmentRepository.findOne(appointmentId));
        }
        return appointment;
    }

    public Set<Appointment> getNextAppointments(AppUser user) {
        Set<Appointment> nextAppointments = Sets.newHashSet();
        if (user != null) {
            nextAppointments = appointmentRepository.getNextAppointments(user, AppointmentStatus.VALID);
        }
        return nextAppointments;
    }
}
