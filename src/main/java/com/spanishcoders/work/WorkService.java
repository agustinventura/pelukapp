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

	public WorkService(WorkRepository workRepository) {
		this.workRepository = workRepository;
	}

	Set<Work> getAvailableWorks(Authentication userAuthentication) {
		if (userAuthentication != null
				&& userAuthentication.getAuthorities().contains(Role.WORKER.getGrantedAuthority())) {
			return Sets.newHashSet(workRepository.findAll());
		} else {
			return Sets.newHashSet(workRepository.findByKind(WorkKind.PUBLIC));
		}
	}

	public Set<Work> get(Set<Integer> workIds) {
		Set<Work> works = Sets.newHashSet();
		if (workIds != null && !workIds.isEmpty()) {
			works = Sets.newHashSet(workRepository.findAll(workIds));
		}
		return works;
	}
}
