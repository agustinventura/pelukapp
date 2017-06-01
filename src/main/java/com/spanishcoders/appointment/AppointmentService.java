package com.spanishcoders.appointment;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserRepository;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockService;

import io.jsonwebtoken.lang.Collections;

@Service
@Transactional
public class AppointmentService {

	private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

	private final AppointmentRepository appointmentRepository;

	private final BlockService blockService;

	private final WorkService workService;

	private final UserRepository userRepository;

	@Value("${max_hours_to_cancel_as_client:24}")
	private int maxHoursToCancelAsClient;

	public AppointmentService(AppointmentRepository appointmentRepository, BlockService blockService,
			WorkService workService, UserRepository userRepository) {
		this.appointmentRepository = appointmentRepository;
		this.blockService = blockService;
		this.workService = workService;
		this.userRepository = userRepository;
	}

	Appointment createAppointment(Authentication authentication, AppointmentDTO appointmentDTO) {
		Appointment confirmed = null;
		final Set<Block> blocks = blockService.get(appointmentDTO.getBlocks());
		final Set<Work> works = workService.get(appointmentDTO.getWorks());
		final AppUser requestUser = authentication != null ? userRepository.findByUsername(authentication.getName())
				: null;
		confirmed = new Appointment(requestUser, works, blocks, appointmentDTO.getNotes());
		confirmed = appointmentRepository.save(confirmed);
		return confirmed;
	}

	Appointment update(Authentication authentication, AppointmentDTO appointment) {
		final Optional<Appointment> maybeAppointment = this.get(appointment.getId());
		Appointment modified = null;
		if (maybeAppointment.isPresent()) {
			modified = modifyNotesOrCancel(authentication, maybeAppointment.get(), appointment);
		} else {
			logger.error(
					"AppUser " + authentication.getName() + " tried to update non-existing appointment " + appointment);
			throw new IllegalArgumentException("There's no Appointment which matches " + appointment);
		}
		return modified;
	}

	private Appointment modifyNotesOrCancel(Authentication authentication, Appointment appointment,
			AppointmentDTO appointmentDTO) {
		checkIfUserIsProprietaryOrAdmin(authentication, appointment);
		final AppointmentStatus dtoStatus = appointmentDTO.getStatus();
		if (dtoStatus == AppointmentStatus.CANCELLED) {
			cancelAppointment(authentication, appointment);
		} else {
			modifyNotes(appointment, appointmentDTO);
		}
		return appointmentRepository.save(appointment);
	}

	private void modifyNotes(Appointment appointment, AppointmentDTO appointmentDTO) {
		appointment.setNotes(appointmentDTO.getNotes());
	}

	private void cancelAppointment(Authentication authentication, Appointment appointment) {
		checkUserPermissionToCancel(authentication, appointment);
		appointment.setBlocks(refreshBlocks(appointment.getBlocks()));
		appointment.cancel();
	}

	private void checkIfUserIsProprietaryOrAdmin(Authentication authentication, Appointment appointment) {
		final AppUser requestUser = authentication != null ? userRepository.findByUsername(authentication.getName())
				: null;
		if (!requestUser.equals(appointment.getUser())) {
			final Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication
					.getAuthorities();
			if (!userAuthorities.stream()
					.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
				throw new AccessDeniedException("To cancel another AppUser Appointments, AppUser needs to be Worker");
			}
		}
	}

	private void checkUserPermissionToCancel(Authentication authentication, Appointment appointment) {
		if (appointment.getDate().isBefore(LocalDateTime.now().plusHours(maxHoursToCancelAsClient))) {
			final Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication
					.getAuthorities();
			if (!userAuthorities.stream()
					.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
				throw new AccessDeniedException(
						"To cancel an Appointment in less than 24 hours, AppUser needs to be Worker");
			}
		}
	}

	private Set<Block> refreshBlocks(Set<Block> requestedBlocks) {
		if (requestedBlocks != null && !requestedBlocks.isEmpty()) {
			final int[] blocksIds = requestedBlocks.stream().mapToInt(block -> block.getId()).toArray();
			requestedBlocks = blockService.get(Collections.arrayToList(blocksIds));
		}
		return requestedBlocks;
	}

	Optional<Appointment> get(Integer appointmentId) {
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
