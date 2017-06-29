package com.spanishcoders.work;

import java.util.Optional;
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

	Work create(Work newWork) {
		return workRepository.save(newWork);
	}

	Optional<Work> get(Integer id) {
		Optional<Work> work = Optional.empty();
		if (id != null) {
			work = Optional.ofNullable(workRepository.findOne(id));
		}
		return work;
	}

	public Work update(String name, WorkKind workKind, WorkStatus workStatus, Work work) {
		// TODO Auto-generated method stub
		return null;
	}
}
