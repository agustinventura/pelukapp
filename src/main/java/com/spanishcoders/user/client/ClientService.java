package com.spanishcoders.user.client;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;

@Service
@Transactional
public class ClientService {

	private final ClientRepository clientRepository;

	private final PasswordEncoder passwordEncoder;

	public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
		super();
		this.clientRepository = clientRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Client createClient(Authentication authentication, Client client) {
		if (authentication == null) {
			// new user registering himself
			createClient(client);
		} else {
			final Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication
					.getAuthorities();
			if (!userAuthorities.stream()
					.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
				// normal user registering another user? not gonna happen
				throw new AccessDeniedException("You need to logout first");
			} else {
				// worker registering user
				createClient(client);
			}
		}

		return client;
	}

	private Client createClient(Client client) {
		checkUsername(client);
		client.setPassword(passwordEncoder.encode(client.getPassword()));
		client = clientRepository.save(client);
		return client;
	}

	private void checkUsername(Client client) {
		final String username = client.getUsername();
		final AppUser existingUser = clientRepository.findByUsername(username);
		if (existingUser != null) {
			throw new IllegalArgumentException("There's an user with username " + username);
		}
	}
}
