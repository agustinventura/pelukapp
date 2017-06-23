package com.spanishcoders.work;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;

@Service
@Transactional(readOnly = true)
public class WorkService {

	private final WorkRepository workRepository;

	public WorkService(WorkRepository workRepository) {
		this.workRepository = workRepository;
	}

	Set<Work> get(AppUser user) {
		Set<Work> availableWorks = null;
		if (user != null && user.getRole().equals(Role.WORKER)) {
			availableWorks = Sets.newHashSet(workRepository.findAll());
		} else {
			availableWorks = Sets.newHashSet(workRepository.findByKindAndStatus(WorkKind.PUBLIC, WorkStatus.ENABLED));
		}
		return availableWorks;
	}

	public Set<Work> get(Set<Integer> workIds) {
		Set<Work> works = Sets.newHashSet();
		if (workIds != null && !workIds.isEmpty()) {
			works = Sets.newHashSet(workRepository.findAll(workIds));
		}
		return works;
	}
}
