package com.spanishcoders.workingday.schedule;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.spanishcoders.user.hairdresser.HairdresserMapper;
import com.spanishcoders.workingday.block.BlockMapper;

@Mapper(uses = { HairdresserMapper.class, BlockMapper.class })
public interface ScheduleMapper {

	Set<HairdresserScheduleDTO> toDTOs(Set<Schedule> schedules);

	@Mappings({ @Mapping(source = "blocks", target = "schedule") })
	HairdresserScheduleDTO toDTO(Schedule schedule);
}
