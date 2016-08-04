package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Client;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Role;
import com.spanishcoders.model.dto.RegistrationDto;
import com.spanishcoders.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by agustin on 21/06/16.
 */
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "appointments/next", method = RequestMethod.GET)
    public Set<Appointment> getNextAppointments(Authentication authentication) {
        Set<Appointment> nextAppointments = userService.getNextAppointments(authentication);
        //TODO: This is a workaround for a lazy loading exception, we need to find a better solution
        if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
            nextAppointments.forEach(appointment -> ((Hairdresser) appointment.getUser()).setAgenda(null));
        }
        return nextAppointments;
    }

    @RequestMapping(value = "register"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Client registerClient(Authentication authentication
            , @RequestBody RegistrationDto registrationDto) {

        Client client = userService.registerClient(authentication
                , registrationDto.getUsername()
                , registrationDto.getPassword()
                , registrationDto.getName()
                , registrationDto.getPhone());

        return client;
    }
}
