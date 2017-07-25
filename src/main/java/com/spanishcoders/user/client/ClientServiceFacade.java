package com.spanishcoders.user.client;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;

@Component
public class ClientServiceFacade {

	private static final Logger logger = Logger.getLogger(ClientServiceFacade.class);

	private final ClientService clientService;

	private final UserService userService;

	private final ClientMapper clientMapper;

	public ClientServiceFacade(ClientService clientService, UserService userService, ClientMapper clientMapper) {
		super();
		this.clientService = clientService;
		this.userService = userService;
		this.clientMapper = clientMapper;
	}

	public ClientDTO create(Authentication authentication, ClientDTO clientDTO) {
		Client client = clientMapper.asEntity(clientDTO);
		if (authentication == null) {
			client = clientService.createClient(client);
		} else {
			final Optional<AppUser> user = userService.get(authentication.getName());
			if (user.isPresent()) {
				client = clientService.createClient(user.get(), client);
			} else {
				logger.error("Unknown user " + authentication.getName() + " tried to register client " + clientDTO);
				throw new AccessDeniedException("Unknown users can't register clients");
			}
		}
		return clientMapper.asDTO(client);
	}

}
