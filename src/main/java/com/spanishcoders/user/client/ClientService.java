package com.spanishcoders.user.client;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;

@Service
@Transactional
public class ClientService {

	private final UserService userService;

	private final ClientRepository clientRepository;

	public ClientService(UserService userService, ClientRepository clientRepository) {
		super();
		this.userService = userService;
		this.clientRepository = clientRepository;
	}

	public Client createClient(Authentication authentication, Client client) {
		if (authentication == null) {
			// new user registering himself
			final AppUser user = userService.create(client);
			client = clientRepository.findOne(user.getId());
		} else {
			final Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication
					.getAuthorities();
			if (!userAuthorities.stream()
					.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
				// normal user registering another user? not gonna happen
				throw new AccessDeniedException("You need to logout first");
			} else {
				// worker registering user
				final AppUser user = userService.create(client);
				client = clientRepository.findOne(user.getId());
			}
		}

		return client;
	}
}
