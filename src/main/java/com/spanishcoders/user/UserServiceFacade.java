package com.spanishcoders.user;

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
		return userMapper.asDTO(userService.get(username));
	}
}
