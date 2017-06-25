package com.spanishcoders.work;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;

@Component
public class WorkServiceFacade {

	private final static Logger logger = Logger.getLogger(WorkServiceFacade.class);
	
	private final WorkService workService;

	private final WorkMapper workMapper;

	private final UserService userService;

	public WorkServiceFacade(WorkService workService, WorkMapper workMapper, UserService userService) {
		super();
		this.workService = workService;
		this.workMapper = workMapper;
		this.userService = userService;
	}

	Set<WorkDTO> get(Authentication authentication) {
		final Set<WorkDTO> dtos = Sets.newHashSet();
		final AppUser user = authentication != null ? userService.get(authentication.getName()) : null;
		if (user != null) {
			dtos.addAll(workMapper.asDTOs(workService.get(user)));
		}
		return dtos;
	}

	WorkDTO create(Authentication authentication, WorkDTO workDTO) {
		checkUser(authentication);
		Work work = workMapper.asWork(workDTO);
		work = workService.create(work);
		return workMapper.asDTO(work);
	}

	private void checkUser(Authentication authentication) {
		final AppUser user = authentication != null ? userService.get(authentication.getName()) : null;
		if (user == null || !user.getRole().equals(Role.WORKER)) {
			logger.error("Can't create a Work without role Worker");
			throw new AccessDeniedException("Can't create a Work without role Worker");
		}
	}

}
