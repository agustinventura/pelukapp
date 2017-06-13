package com.spanishcoders.work;

import java.util.Collection;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/works", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkController {

	private final WorkServiceFacade workServiceFacade;

	public WorkController(WorkServiceFacade workServiceFacade) {
		this.workServiceFacade = workServiceFacade;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.GET)
	public Collection<WorkDTO> getWorks(Authentication authentication) {
		return workServiceFacade.get(authentication);
	}
}
