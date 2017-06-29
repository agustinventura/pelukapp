package com.spanishcoders.appointment;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.spanishcoders.work.WorkMapper;
import com.spanishcoders.workingday.block.BlockMapper;

@Mapper(uses = { BlockMapper.class, WorkMapper.class })
public interface AppointmentMapper {

	@Mapping(target = "user", source = "user.id")
	AppointmentDTO asDTO(Appointment appointment);

	Set<AppointmentDTO> asDTOs(Set<Appointment> appointments);
}
