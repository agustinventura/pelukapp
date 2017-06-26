package com.spanishcoders.error;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ErrorMapper {

	default ErrorsDTO toDTO(ConstraintViolationException ex) {
		final ErrorsDTO errors = new ErrorsDTO();
		for (final ConstraintViolation violation : ex.getConstraintViolations()) {
			errors.add(new ErrorDTO(violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return errors;
	}

}
