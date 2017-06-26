package com.spanishcoders.error;

public class ErrorDTO {

	private final String attribute;

	private final String message;

	public ErrorDTO() {
		super();
		this.attribute = "";
		this.message = "";
	}

	public ErrorDTO(String attribute, String message) {
		super();
		this.attribute = attribute;
		this.message = message;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		final ErrorDTO other = (ErrorDTO) obj;
		if (attribute == null) {
			if (other.attribute != null) {
				return false;
			}
		} else if (!attribute.equals(other.attribute)) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ErrorDTO [attribute=" + attribute + ", message=" + message + "]";
	}
}
