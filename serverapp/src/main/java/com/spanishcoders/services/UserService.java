package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.User;
import com.spanishcoders.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by agustin on 16/07/16.
 */
@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Set<Appointment> getNextAppointmnents(Authentication authentication) {
        Set<Appointment> appointments = null;
        if (authentication == null) {
            throw new AccessDeniedException("Can't get next appointments without Authentication");
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            throw new AccessDeniedException("User " + authentication.getName() + " does not exists");
        } else {
            appointments = Sets.newTreeSet(user.getAppointments());
        }
        return appointments;
    }
}
