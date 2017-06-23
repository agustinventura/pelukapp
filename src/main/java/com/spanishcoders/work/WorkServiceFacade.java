package com.spanishcoders.work;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;

@Component
public class WorkServiceFacade {

	private final WorkService workService;

	private final WorkMapper workMapper;

	private final UserService userService;

	public WorkServiceFacade(WorkService workService, WorkMapper workMapper, UserService userService) {
		super();
		this.workService = workService;
		this.workMapper = workMapper;
		this.userService = userService;
	}

	public Set<WorkDTO> get(Authentication authentication) {
		final Set<WorkDTO> dtos = Sets.newHashSet();
		final AppUser user = authentication != null ? userService.get(authentication.getName()) : null;
		if (user != null) {
			dtos.addAll(workMapper.asDTOs(workService.get(user)));
		}
		return dtos;
	}
}
