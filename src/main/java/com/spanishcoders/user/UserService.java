package com.spanishcoders.user;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.appointment.AppointmentService;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.client.ClientDTO;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AppointmentService appointmentService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AppointmentService appointmentService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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

	public Client createClient(Authentication authentication, ClientDTO clientDTO) {
		final Client client = null;
		if (authentication == null) {
			// new user registering himself
			createClient(clientDTO);
		} else {
			final Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication
					.getAuthorities();
			if (!userAuthorities.stream()
					.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
				// normal user registering another user? not gonna happen
				throw new AccessDeniedException("You need to logout first");
			} else {
				// worker registering user
				createClient(clientDTO);
			}
		}

		return client;
	}

	private Client createClient(ClientDTO clientDTO) {
		checkUsername(clientDTO);
		clientDTO.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
		final Client client = new Client(clientDTO);
		userRepository.save(client);
		return client;
	}

	private void checkUsername(UserDTO userDTO) {
		final String username = userDTO.getUsername();
		final AppUser existingUser = userRepository.findByUsername(username);
		if (existingUser != null) {
			throw new IllegalArgumentException("There's an user with username " + username);
		}
	}

}
