package com.spanishcoders.user.client;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@RequestMapping(value = "client", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ClientDTO registerClient(Authentication authentication, @RequestBody ClientDTO clientDTO) {
		return new ClientDTO(clientService.createClient(authentication, clientDTO));
	}
}
