package com.spanishcoders.workingday.block;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.google.common.collect.Sets;
import com.spanishcoders.workingday.schedule.ScheduleDTO;

@Mapper(componentModel = "spring")
public interface BlockMapper {

	default Integer asInteger (Block block) {
		return block.getId();
	}
	
	Set<Integer> asIntegers (Set<Block> blocks);
	
	Set<ScheduleDTO> asScheduleDTOs (Set<Block> blocks);
	
	default ScheduleDTO asScheduleDTO (Block block) {
		ScheduleDTO dto = null;
		if (block != null) {
			dto = new ScheduleDTO();
			Integer blockId = block.getId();
			LocalTime start = block.getStart();
			Duration length = block.getLength();
			LocalDate workingDay = block.getWorkingDay() != null ? block.getWorkingDay().getDate() : null;
			Integer hairdresserId = block.getWorkingDay().getAgenda().getHairdresser().getId();
			Integer appointmentId = block.getAppointment() != null ? block.getAppointment().getId() : 0;
			String client = block.getAppointment() != null ? block.getAppointment().getUser().getName() : "";
			Set<Integer> worksIds = Sets.newHashSet();
			if (block.getAppointment() != null) {
				worksIds.addAll(block.getAppointment().getWorks().stream().map(work -> work.getId())
						.collect(Collectors.toSet()));
			}
			String notes = block.getAppointment() != null ? block.getAppointment().getNotes() : "";
			dto = new ScheduleDTO(blockId, start, length, workingDay, hairdresserId, appointmentId, client, worksIds, notes);
		}
		return dto;
	}
}
