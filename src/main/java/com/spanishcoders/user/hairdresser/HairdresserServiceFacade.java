package com.spanishcoders.user.hairdresser;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.spanishcoders.workingday.schedule.HairdresserScheduleDTO;
import com.spanishcoders.workingday.schedule.Schedule;
import com.spanishcoders.workingday.schedule.ScheduleMapper;

@Component
public class HairdresserServiceFacade {

	private final HairdresserService hairdresserService;

	private final HairdresserMapper hairdresserMapper;

	private final ScheduleMapper scheduleMapper;

	public HairdresserServiceFacade(HairdresserService hairdresserService, HairdresserMapper hairdresserMapper,
			ScheduleMapper scheduleMapper) {
		super();
		this.hairdresserService = hairdresserService;
		this.hairdresserMapper = hairdresserMapper;
		this.scheduleMapper = scheduleMapper;
	}

	public HairdresserDTO create(Authentication authentication, HairdresserDTO hairdresserDTO) {
		Hairdresser hairdresser = hairdresserMapper.asEntity(hairdresserDTO);
		return hairdresserMapper.asDTO(hairdresserService.registerHairdresser(authentication, hairdresser));
	}

	public Set<HairdresserScheduleDTO> getSchedule(LocalDate day) {
		final Set<Schedule> schedules = hairdresserService.getSchedule(day);
		return scheduleMapper.toDTOs(schedules);
	}

}
