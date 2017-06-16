package com.spanishcoders.user.client;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ClientServiceFacade {

	private final ClientService clientService;

	private final ClientMapper clientMapper;

	public ClientServiceFacade(ClientService clientService, ClientMapper clientMapper) {
		super();
		this.clientService = clientService;
		this.clientMapper = clientMapper;
	}

	public ClientDTO create(Authentication authentication, Client client) {
		return clientMapper.asDTO(clientService.createClient(authentication, client));
	}

}
