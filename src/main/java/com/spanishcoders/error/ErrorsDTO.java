package com.spanishcoders.error;

import java.util.List;

import com.google.common.collect.Lists;

public class ErrorsDTO {

	private final List<ErrorDTO> errors;

	public ErrorsDTO() {
		super();
		this.errors = Lists.newArrayList();
	}

	public List<ErrorDTO> getErrors() {
		return errors;
	}

	public void add(ErrorDTO newError) {
		this.errors.add(newError);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ErrorsDTO other = (ErrorsDTO) obj;
		if (errors == null) {
			if (other.errors != null) {
				return false;
			}
		} else if (!errors.equals(other.errors)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ErrorsDTO [errors=" + errors + "]";
	}
}
