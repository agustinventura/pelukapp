package com.spanishcoders.appointment;

import java.util.Set;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

	AppointmentDTO asDTO(Appointment appointment);

	Set<AppointmentDTO> asDTOs(Set<Appointment> appointments);
}
