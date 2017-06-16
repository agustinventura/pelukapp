package com.spanishcoders.user.hairdresser;

import org.mapstruct.Mapper;

import com.spanishcoders.agenda.AgendaMapper;
import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(componentModel = "spring", uses = {AgendaMapper.class, AppointmentMapper.class})
public interface HairdresserMapper {

	HairdresserDTO asDTO(Hairdresser hairdresser);
}
