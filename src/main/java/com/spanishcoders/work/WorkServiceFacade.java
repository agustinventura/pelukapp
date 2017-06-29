package com.spanishcoders.work;

import java.util.Optional;
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
		final Optional<AppUser> user = authentication != null ? userService.get(authentication.getName())
				: Optional.empty();
		if (user.isPresent()) {
			dtos.addAll(workMapper.asDTOs(workService.get(user.get())));
		}
		return dtos;
	}

	WorkDTO create(Authentication authentication, WorkDTO workDTO) {
		checkUser(authentication);
		Work work = workMapper.asWork(workDTO);
		work = workService.create(work);
		return workMapper.asDTO(work);
	}

	WorkDTO update(Authentication authentication, WorkDTO workDTO) {
		final AppUser user = checkUser(authentication);
		final Optional<Work> original = workService.get(workDTO.getId());
		Work modified = null;
		if (original.isPresent()) {
			modified = workService.update(workDTO.getName(), workDTO.getWorkKind(), workDTO.getWorkStatus(),
					original.get());
		} else {
			logger.error("AppUser " + user.getUsername() + " tried to update non-existing work " + workDTO);
			throw new IllegalArgumentException("There's no Work which matches " + workDTO);
		}
		return workMapper.asDTO(modified);
	}

	private AppUser checkUser(Authentication authentication) {
		final Optional<AppUser> user = authentication != null ? userService.get(authentication.getName())
				: Optional.empty();
		if (!user.isPresent() || !user.get().getRole().equals(Role.WORKER)) {
			logger.error("Can't create a Work without role Worker");
			throw new AccessDeniedException("Can't create a Work without role Worker");
		}
		return user.get();
	}

}
