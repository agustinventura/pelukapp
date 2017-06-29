package com.spanishcoders.user;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.appointment.AppointmentService;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final AppointmentService appointmentService;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, AppointmentService appointmentService,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.appointmentService = appointmentService;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(readOnly = false)
	public AppUser create(AppUser user) {
		checkUsername(user);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user = userRepository.save(user);
		return user;
	}

	public AppUser get(Integer id) {
		return userRepository.findOne(id);
	}

	private void checkUsername(AppUser user) {
		final String username = user.getUsername();
		final Optional<AppUser> existingUser = get(username);
		if (existingUser.isPresent()) {
			throw new IllegalArgumentException("There's an user with username " + username);
		}
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

	public Optional<AppUser> get(String username) {
		Optional<AppUser> user = Optional.empty();
		if (!StringUtils.isEmpty(username)) {
			user = Optional.ofNullable(userRepository.findByUsername(username));
		}
		return user;
	}
}
