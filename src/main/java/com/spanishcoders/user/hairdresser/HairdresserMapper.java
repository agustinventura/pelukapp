package com.spanishcoders.user.hairdresser;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(uses = { AppointmentMapper.class })
public interface HairdresserMapper {

	@Mapping(target = "password", constant = "")
	HairdresserDTO asDTO(Hairdresser hairdresser);

	@Mappings({ @Mapping(target = "appointments", ignore = true), @Mapping(target = "agenda", ignore = true) })
	Hairdresser asEntity(HairdresserDTO hairdresserDTO);
}
