package com.spanishcoders.user.client;

import org.mapstruct.Mapper;

import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(componentModel = "spring", uses = {AppointmentMapper.class})
public interface ClientMapper {

	ClientDTO asDTO(Client client);
}
