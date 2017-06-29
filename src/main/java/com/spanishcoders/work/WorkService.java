package com.spanishcoders.work;

import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;

@Service
@Transactional(readOnly = true)
public class WorkService {

	private static final Logger logger = Logger.getLogger(WorkService.class);

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
		Work modified = null;
		final Optional<Work> toBeModified = this.get(work.getId());
		if (toBeModified.isPresent()) {
			modified = toBeModified.get();
			modified.setName(name);
			modified.setKind(workKind);
			modified.setStatus(workStatus);
			modified = workRepository.save(modified);
		} else {
			logger.error("Tried to update non existing Work: " + work);
			throw new IllegalArgumentException("Tried to update non existing Work: " + work);
		}
		return modified;
	}
}
