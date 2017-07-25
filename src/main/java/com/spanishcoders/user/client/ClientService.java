package com.spanishcoders.user.client;

import org.springframework.security.access.AccessDeniedException;
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

	public Client createClient(AppUser user, Client client) {
		if (user.getRole() == Role.CLIENT) {
			// normal user registering another user? not gonna happen
			throw new AccessDeniedException("You need to logout first");
		}
		// worker registering user
		return createClient(client);
	}

	public Client createClient(Client client) {
		// new user registering himself
		if (client == null) {
			throw new IllegalArgumentException("Can't create a client without data");
		}
		final AppUser user = userService.create(client);
		client = clientRepository.findOne(user.getId());
		return client;
	}
}
