package com.spanishcoders.work;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.user.Role;

@Service
@Transactional(readOnly = true)
public class WorkService {

	private final WorkRepository workRepository;

	private final WorkMapper workMapper;

	public WorkService(WorkRepository workRepository, WorkMapper workMapper) {
		this.workRepository = workRepository;
		this.workMapper = workMapper;
	}

	Set<WorkDTO> getAvailableWorks(Authentication userAuthentication) {
		Set<Work> availableWorks = null;
		if (userAuthentication != null
				&& userAuthentication.getAuthorities().contains(Role.WORKER.getGrantedAuthority())) {
			availableWorks = Sets.newHashSet(workRepository.findAll());
		} else {
			availableWorks = Sets.newHashSet(workRepository.findByKind(WorkKind.PUBLIC));
		}
		return workMapper.asDTOs(availableWorks);
	}

	public Set<Work> get(Set<Integer> workIds) {
		Set<Work> works = Sets.newHashSet();
		if (workIds != null && !workIds.isEmpty()) {
			works = Sets.newHashSet(workRepository.findAll(workIds));
		}
		return works;
	}
}
