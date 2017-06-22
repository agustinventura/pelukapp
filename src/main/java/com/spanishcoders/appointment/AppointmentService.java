package com.spanishcoders.appointment;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.work.Work;
import com.spanishcoders.workingday.block.Block;

@Service
@Transactional(readOnly = true)
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;

	@Value("${max_hours_to_cancel_as_client:24}")
	private int maxHoursToCancelAsClient;

	public AppointmentService(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	@Transactional(readOnly = false)
	Appointment createAppointment(AppUser user, Set<Block> blocks, Set<Work> works, String notes) {
		Appointment confirmed = new Appointment(user, works, blocks, notes);
		confirmed = appointmentRepository.save(confirmed);
		return confirmed;
	}

	@Transactional(readOnly = false)
	Appointment update(AppUser user, AppointmentStatus newStatus, String notes, Appointment appointment) {
		final Appointment modified = modifyNotesOrCancel(user, newStatus, notes, appointment);
		return modified;
	}

	private Appointment modifyNotesOrCancel(AppUser user, AppointmentStatus newStatus, String notes,
			Appointment appointment) {
		checkIfUserIsProprietaryOrAdmin(user, appointment);
		if (newStatus == AppointmentStatus.CANCELLED) {
			cancelAppointment(user, appointment);
		} else {
			modifyNotes(notes, appointment);
		}
		return appointmentRepository.save(appointment);
	}

	private void modifyNotes(String notes, Appointment appointment) {
		appointment.setNotes(notes);
	}

	private void cancelAppointment(AppUser user, Appointment appointment) {
		checkUserPermissionToCancel(user, appointment);
		appointment.cancel();
	}

	private void checkIfUserIsProprietaryOrAdmin(AppUser user, Appointment appointment) {
		if (!user.equals(appointment.getUser())) {
			if (!user.getRole().equals(Role.WORKER)) {
				throw new AccessDeniedException("To modify another AppUser Appointments, AppUser needs to be Worker");
			}
		}
	}

	private void checkUserPermissionToCancel(AppUser user, Appointment appointment) {
		if (appointment.getDate().isBefore(LocalDateTime.now().plusHours(maxHoursToCancelAsClient))) {
			if (!user.getRole().equals(Role.WORKER)) {
				throw new AccessDeniedException(
						"To cancel an Appointment in less than 24 hours, AppUser needs to be Worker");
			}
		}
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
