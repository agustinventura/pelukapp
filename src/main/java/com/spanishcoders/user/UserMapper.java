package com.spanishcoders.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(componentModel = "spring", uses = { AppointmentMapper.class })
public interface UserMapper {

	@Mapping(target = "password", constant = "")
	UserDTO asDTO(AppUser user);
}
