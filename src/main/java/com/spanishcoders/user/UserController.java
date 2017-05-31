package com.spanishcoders.user;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
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

import com.spanishcoders.appointment.AppointmentDTO;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.client.ClientDTO;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	private final static Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(value = "appointments/next", method = RequestMethod.GET)
	public Set<AppointmentDTO> getNextAppointments(Authentication authentication) {
		return userService.getNextAppointments(authentication).stream()
				.map(appointment -> new AppointmentDTO(appointment)).collect(Collectors.toSet());
	}

	@RequestMapping(value = "client", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Client registerClient(Authentication authentication, @RequestBody ClientDTO clientDTO) {
		return userService.createClient(authentication, clientDTO);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity illegalArgumentExceptionHandler(HttpServletRequest req, Exception ex) {
		logger.error("Caught IllegalArgumentException processing AppUser request " + req.getRequestURL() + ": "
				+ ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity accessDeniedExceptionExceptionHandler(HttpServletRequest req, Exception ex) {
		logger.error("Caught AccessDeniedException processing AppUser request " + req.getRequestURL() + ": "
				+ ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

}
