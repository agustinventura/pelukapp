package com.spanishcoders.services;

import com.spanishcoders.model.*;
import com.spanishcoders.model.dto.ClientDTO;
import com.spanishcoders.model.dto.UserDTO;
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


    public Client createClient(Authentication authentication, ClientDTO clientDTO) {
        Client client = null;
        if (authentication == null) {
            // new user registering himself
            createClient(clientDTO);
        } else {
            Collection<GrantedAuthority> userAuthorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
            if (!userAuthorities.stream().anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
                // normal user registering another user? not gonna happen
                throw new AccessDeniedException("You need to logout first");
            } else {
                // worker registering user
                createClient(clientDTO);
            }
        }

        return client;
    }

    private Client createClient(ClientDTO clientDTO) {
        checkUsername(clientDTO);
        clientDTO.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        Client client = new Client(clientDTO);
        userRepository.save(client);
        return client;
    }

    public Hairdresser registerHairdresser(Authentication authentication, UserDTO userDTO) {
        if (authentication == null) {
            throw new AccessDeniedException("User needs to be logged to register a hairdresser");
        } else {
            if (userDTO == null) {
                throw new IllegalArgumentException("Can't create a hairdresser from null data");
            } else {
                return createHairdresser(userDTO);
            }
        }
    }

    private Hairdresser createHairdresser(UserDTO userDTO) {
        checkUsername(userDTO);
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Hairdresser hairdresser = new Hairdresser(userDTO);
        return userRepository.save(hairdresser);
    }

    private void checkUsername(UserDTO userDTO) {
        String username = userDTO.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("There's an user with username " + username);
        }
    }

}
