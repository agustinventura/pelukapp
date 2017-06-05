package com.spanishcoders.user.hairdresser;

import com.spanishcoders.user.UserDTO;

public class HairdresserDTO extends UserDTO {

	private Integer agenda;

	public HairdresserDTO() {
		super();
	}

	public HairdresserDTO(Hairdresser hairdresser) {
		super(hairdresser);
		this.setAgenda(hairdresser.getAgenda() != null ? hairdresser.getAgenda().getId() : null);
	}

	public Integer getAgenda() {
		return agenda;
	}

	public void setAgenda(Integer agenda) {
		this.agenda = agenda;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		final HairdresserDTO that = (HairdresserDTO) o;

		return agenda != null ? agenda.equals(that.agenda) : that.agenda == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (agenda != null ? agenda.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "HairdresserDTO{" + "agenda=" + agenda + '}';
	}
}
