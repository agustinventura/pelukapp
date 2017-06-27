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

	default Integer asInteger(Block block) {
		return block.getId();
	}

	Set<Integer> asIntegers(Set<Block> blocks);

	default Block asBlock(Integer id) {
		final Block block = new Block();
		block.setId(id);
		return block;
	}

	Set<Block> asBlocks(Set<Integer> ids);

	Set<ScheduleDTO> asScheduleDTOs(Set<Block> blocks);

	default ScheduleDTO asScheduleDTO(Block block) {
		ScheduleDTO dto = null;
		if (block != null) {
			dto = new ScheduleDTO();
			final Integer blockId = block.getId();
			final LocalTime start = block.getStart();
			final Duration length = block.getLength();
			final LocalDate workingDay = block.getWorkingDay() != null ? block.getWorkingDay().getDate() : null;
			final Integer hairdresserId = block.getWorkingDay().getAgenda().getHairdresser().getId();
			final Integer appointmentId = block.getAppointment() != null ? block.getAppointment().getId() : 0;
			final String client = block.getAppointment() != null
					? block.getAppointment().getUser() != null ? block.getAppointment().getUser().getName() : "" : "";
			final Set<Integer> worksIds = Sets.newHashSet();
			if (block.getAppointment() != null && block.getAppointment().getWorks() != null) {
				worksIds.addAll(block.getAppointment().getWorks().stream().map(work -> work.getId())
						.collect(Collectors.toSet()));
			}
			final String notes = block.getAppointment() != null ? block.getAppointment().getNotes() : "";
			dto = new ScheduleDTO(blockId, start, length, workingDay, hairdresserId, appointmentId, client, worksIds,
					notes);
		}
		return dto;
	}
}
