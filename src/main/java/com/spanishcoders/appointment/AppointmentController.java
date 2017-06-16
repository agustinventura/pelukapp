package com.spanishcoders.appointment;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/appointment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentController {

	private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

	private final AppointmentServiceFacade appointmentServiceFacade;

	public AppointmentController(AppointmentServiceFacade appointmentServiceFacade) {
		this.appointmentServiceFacade = appointmentServiceFacade;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.POST)
	public AppointmentDTO create(Authentication authentication, @RequestBody AppointmentDTO appointment) {
		return appointmentServiceFacade.create(authentication, appointment);
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.PUT)
	public AppointmentDTO update(Authentication authentication, @RequestBody AppointmentDTO appointment) {
		return appointmentServiceFacade.update(authentication, appointment);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity illegalArgumentExceptionHandler(HttpServletRequest req, Exception ex) {
		logger.error("Caught IllegalArgumentException processing Appointment request " + req.getRequestURL() + ": "
				+ ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity accessDeniedExceptionExceptionHandler(HttpServletRequest req, Exception ex) {
		logger.error("Caught AccessDeniedException processing Appointment request " + req.getRequestURL() + ": "
				+ ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
}
