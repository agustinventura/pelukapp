package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        Set<Appointment> nextAppointmnets = userService.getNextAppointmnents(authentication);
        return nextAppointmnets;
    }
}
