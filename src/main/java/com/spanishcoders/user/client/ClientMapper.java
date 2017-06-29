package com.spanishcoders.user.client;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(uses = AppointmentMapper.class)
public interface ClientMapper {

	@Mapping(target = "password", constant = "")
	ClientDTO asDTO(Client client);

	@Mapping(target = "appointments", ignore = true)
	Client asEntity(ClientDTO clientDTO);
}
