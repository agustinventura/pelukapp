package com.spanishcoders.work;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.user.Role;

/**
 * Created by agustin on 14/06/16.
 */
@Service
@Transactional
public class WorkService {

	private final WorkRepository workRepository;

	public WorkService(WorkRepository workRepository) {
		this.workRepository = workRepository;
	}

	Set<Work> getAvailableWorks(Collection<? extends GrantedAuthority> authorities) {
		if (authorities.contains(Role.WORKER.getGrantedAuthority())) {
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
