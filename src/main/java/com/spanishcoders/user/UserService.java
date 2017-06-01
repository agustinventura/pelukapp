package com.spanishcoders.user;

import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.appointment.AppointmentService;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final AppointmentService appointmentService;

	public UserService(UserRepository userRepository, AppointmentService appointmentService) {
		this.userRepository = userRepository;
		this.appointmentService = appointmentService;
	}

	Set<Appointment> getNextAppointments(Authentication authentication) {
		Set<Appointment> appointments = null;
		if (authentication == null) {
			throw new AccessDeniedException("Can't get next appointments without Authentication");
		}
		final AppUser user = userRepository.findByUsername(authentication.getName());
		if (user == null) {
			throw new AccessDeniedException("AppUser " + authentication.getName() + " does not exists");
		} else {
			appointments = appointmentService.getNextAppointments(user);
		}
		return appointments;
	}
}
