package com.spanishcoders.workingday.schedule;

import java.util.Set;

import com.google.common.collect.Sets;
import com.spanishcoders.user.hairdresser.HairdresserDTO;

public class HairdresserScheduleDTO {

	private HairdresserDTO hairdresser;

	private Set<ScheduleDTO> schedule;

	public HairdresserScheduleDTO() {
		hairdresser = new HairdresserDTO();
		schedule = Sets.newHashSet();
	}

	public HairdresserScheduleDTO(HairdresserDTO hairdresser, Set<ScheduleDTO> schedule) {
		this.hairdresser = hairdresser;
		this.schedule = schedule;
	}

	public HairdresserDTO getHairdresser() {
		return hairdresser;
	}

	public void setHairdresser(HairdresserDTO hairdresser) {
		this.hairdresser = hairdresser;
	}

	public Set<ScheduleDTO> getSchedule() {
		return schedule;
	}

	public void setSchedule(Set<ScheduleDTO> schedule) {
		this.schedule = schedule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hairdresser == null) ? 0 : hairdresser.hashCode());
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
		final HairdresserScheduleDTO other = (HairdresserScheduleDTO) obj;
		if (hairdresser == null) {
			if (other.hairdresser != null) {
				return false;
			}
		} else if (!hairdresser.equals(other.hairdresser)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "HairdresserScheduleDTO [hairdresser=" + hairdresser + ", schedule=" + schedule + "]";
	}
}
