package com.spanishcoders.user;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class UserServiceFacade {

	private final UserService userService;

	private final UserMapper userMapper;

	public UserServiceFacade(UserService userService, UserMapper userMapper) {
		super();
		this.userService = userService;
		this.userMapper = userMapper;
	}

	public UserDTO get(String username) {
		UserDTO dto = null;
		final Optional<AppUser> user = userService.get(username);
		if (user.isPresent()) {
			dto = userMapper.asDTO(user.get());
		}
		return dto;
	}
}
