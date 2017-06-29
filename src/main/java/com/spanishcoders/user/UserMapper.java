package com.spanishcoders.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.spanishcoders.appointment.AppointmentMapper;

@Mapper(uses = { AppointmentMapper.class })
public interface UserMapper {

	@Mapping(target = "password", constant = "")
	UserDTO asDTO(AppUser user);

	default AppUser asUser(Integer id) {
		AppUser user = null;
		if (id != null) {
			user = new AppUser();
			user.setId(id);
		}
		return user;
	}

	default Integer asInteger(AppUser user) {
		Integer userId = null;
		if (user != null) {
			userId = user.getId();
		}
		return userId;
	}
}
