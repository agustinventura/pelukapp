package com.spanishcoders.workingday.schedule;

import java.util.Set;

import org.mapstruct.Mapper;

import com.spanishcoders.user.hairdresser.HairdresserMapper;

@Mapper(componentModel = "spring", uses = HairdresserMapper.class)
public interface ScheduleMapper {

	Set<HairdresserScheduleDTO> toDTO(Set<Schedule> schedules);

}
