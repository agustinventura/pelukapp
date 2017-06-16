package com.spanishcoders.work;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

@Component
public class WorkServiceFacade {

	private final WorkService workService;

	private final WorkMapper workMapper;

	public WorkServiceFacade(WorkService workService, WorkMapper workMapper) {
		super();
		this.workService = workService;
		this.workMapper = workMapper;
	}

	public Set<WorkDTO> get(Authentication authentication) {
		final Set<WorkDTO> dtos = Sets.newHashSet();
		if (authentication != null) {
			dtos.addAll(workMapper.asDTOs(workService.get(authentication)));
		}
		return dtos;
	}
}
