package com.spanishcoders.appointment;

import java.util.Set;

import org.mapstruct.Mapper;

import com.spanishcoders.user.UserMapper;
import com.spanishcoders.work.WorkMapper;
import com.spanishcoders.workingday.block.BlockMapper;

@Mapper(componentModel = "spring", uses = {BlockMapper.class, WorkMapper.class, UserMapper.class})
public interface AppointmentMapper {

	AppointmentDTO asDTO(Appointment appointment);

	Set<AppointmentDTO> asDTOs(Set<Appointment> appointments);
	
	Appointment asEntity(AppointmentDTO dto);
	
	Set<Appointment> asEntities(Set<AppointmentDTO> dto);
}
