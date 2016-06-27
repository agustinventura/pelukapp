package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Role;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import com.spanishcoders.repositories.WorkRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * Created by agustin on 14/06/16.
 */
@Service
public class WorkService {

    private WorkRepository workRepository;

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public Set<Work> getAvailableWorks(Collection<? extends GrantedAuthority> authorities) {
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
