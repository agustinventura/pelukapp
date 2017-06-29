package com.spanishcoders.user.hairdresser;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.spanishcoders.agenda.AgendaMapper;
import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(uses = { AgendaMapper.class, AppointmentMapper.class })
public interface HairdresserMapper {

	@Mapping(target = "password", constant = "")
	HairdresserDTO asDTO(Hairdresser hairdresser);

	@Mapping(target = "appointments", ignore = true)
	Hairdresser asEntity(HairdresserDTO hairdresserDTO);
}
