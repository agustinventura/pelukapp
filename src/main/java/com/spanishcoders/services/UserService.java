package com.spanishcoders.services;

import com.spanishcoders.model.*;
import com.spanishcoders.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

/**
 * Created by agustin on 16/07/16.
 */
@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AppointmentService appointmentService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AppointmentService appointmentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appointmentService = appointmentService;
    }

    public Set<Appointment> getNextAppointments(Authentication authentication) {
        Set<Appointment> appointments = null;
        if (authentication == null) {
            throw new AccessDeniedException("Can't get next appointments without Authentication");
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            throw new AccessDeniedException("User " + authentication.getName() + " does not exists");
        } else {
            appointments = appointmentService.getNextAppointments(user);
        }
        return appointments;
    }


    public Client registerClient(Authentication authentication, String username, String password, String name, String phone) {

        Client client = null;

        if (authentication == null) {
            // new user registering himself
            registerClient(username, password, name, phone);

        } else {

            Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
            if (!userAuthorities.stream().anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
                // normal user registering another user? not gonna happen
                throw new AccessDeniedException("You need to logout first");

            } else {
                // worker registering user
                registerClient(username, password, name, phone);
            }

        }

        return client;
    }

    private Client registerClient(String username, String password, String name, String phone) {
        Client client = new Client();
        client.setName(name);
        client.setUsername(username);
        client.setPassword(passwordEncoder.encode(password));
        client.setPhone(phone);
        client.setStatus(UserStatus.ACTIVE);

        userRepository.save(client);

        return client;
    }

}
