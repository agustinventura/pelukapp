package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import com.spanishcoders.repositories.WorkRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_WORKER"))) {
            return Sets.newHashSet(workRepository.findAll());
        } else {
            return Sets.newHashSet(workRepository.findByKind(WorkKind.PUBLIC));
        }
    }

}
