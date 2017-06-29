package com.spanishcoders.error;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.mapstruct.Mapper;

@Mapper
public interface ErrorMapper {

	default ErrorsDTO toDTO(ConstraintViolationException ex) {
		final ErrorsDTO errors = new ErrorsDTO();
		if (ex != null) {
			for (final ConstraintViolation violation : ex.getConstraintViolations()) {
				errors.add(new ErrorDTO(violation.getPropertyPath().toString(), violation.getMessage()));
			}
		}
		return errors;
	}

}
