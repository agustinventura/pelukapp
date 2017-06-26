package com.spanishcoders.error;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestErrorHandler {

	private static final Logger logger = Logger.getLogger(RestErrorHandler.class);

	private final ErrorMapper errorMapper;

	public RestErrorHandler(ErrorMapper errorMapper) {
		super();
		this.errorMapper = errorMapper;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDTO processApplicationValidationError(HttpServletRequest req, ConstraintViolationException ex) {
		final ErrorsDTO errors = errorMapper.toDTO(ex);
		log(req, ex, errors);
		return errors;
	}

	private void log(HttpServletRequest req, ConstraintViolationException ex, ErrorsDTO errors) {
		logger.error("ContraintViolationException accessing URL " + req.getRequestURL() + ": " + errors);
	}
}
