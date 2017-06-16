package com.spanishcoders.appointment;

import java.util.Set;

import org.mapstruct.Mapper;

import com.spanishcoders.work.WorkMapper;
import com.spanishcoders.workingday.block.BlockMapper;

@Mapper(componentModel = "spring", uses = {BlockMapper.class, WorkMapper.class})
public interface AppointmentMapper {

	AppointmentDTO asDTO(Appointment appointment);

	Set<AppointmentDTO> asDTOs(Set<Appointment> appointments);
}
