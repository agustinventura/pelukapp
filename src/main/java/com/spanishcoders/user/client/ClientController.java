package com.spanishcoders.user.client;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/client", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ClientDTO registerClient(Authentication authentication, @RequestBody ClientDTO clientDTO) {
		final Client client = new Client(clientDTO);
		return new ClientDTO(clientService.createClient(authentication, client));
	}
}
