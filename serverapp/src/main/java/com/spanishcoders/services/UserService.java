package com.spanishcoders.services;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by agustin on 16/07/16.
 */
@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Set<Appointment> getNextAppointmnents(Authentication authentication) {
        return null;
    }
}
