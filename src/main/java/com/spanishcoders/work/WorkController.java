package com.spanishcoders.work;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/works",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkController {

	private static final Logger logger = Logger.getLogger(WorkController.class);
	
	private final WorkServiceFacade workServiceFacade;

	public WorkController(WorkServiceFacade workServiceFacade) {
		this.workServiceFacade = workServiceFacade;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.GET)
	public Collection<WorkDTO> getWorks(Authentication authentication) {
		return workServiceFacade.get(authentication);
	}
	
	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public WorkDTO create(Authentication authentication, @RequestBody WorkDTO workDTO) {
		return workServiceFacade.create(authentication, workDTO);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity accessDeniedExceptionExceptionHandler(HttpServletRequest req, Exception ex) {
		logger.error("Caught AccessDeniedException processing Work request " + req.getRequestURL() + ": "
				+ ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
}
