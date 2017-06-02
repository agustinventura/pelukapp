package com.spanishcoders.user.client;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;

@Service
@Transactional
public class ClientService {

	private final UserService userService;

	public ClientService(UserService userService) {
		super();
		this.userService = userService;
	}

	public Client createClient(Authentication authentication, Client client) {
		if (authentication == null) {
			// new user registering himself
			userService.create(client);
		} else {
			final Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication
					.getAuthorities();
			if (!userAuthorities.stream()
					.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
				// normal user registering another user? not gonna happen
				throw new AccessDeniedException("You need to logout first");
			} else {
				// worker registering user
				userService.create(client);
			}
		}

		return client;
	}
}
