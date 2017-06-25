package com.spanishcoders.user.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/client", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

	private final ClientServiceFacade clientServiceFacade;

	public ClientController(ClientServiceFacade clientServiceFacade) {
		this.clientServiceFacade = clientServiceFacade;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ClientDTO registerClient(Authentication authentication, @RequestBody ClientDTO clientDTO) {
		return clientServiceFacade.create(authentication, clientDTO);
	}
}
