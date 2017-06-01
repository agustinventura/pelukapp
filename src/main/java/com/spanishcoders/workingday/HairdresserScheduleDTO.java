package com.spanishcoders.workingday;

import java.util.Set;

import com.google.common.collect.Sets;
import com.spanishcoders.user.hairdresser.HairdresserDTO;

public class HairdresserScheduleDTO {

	private final HairdresserDTO hairdresser;

	private final Set<ScheduleDTO> schedule;

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

	public Set<ScheduleDTO> getSchedule() {
		return schedule;
	}
}
