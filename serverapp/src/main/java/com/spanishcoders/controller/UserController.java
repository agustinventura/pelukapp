package com.spanishcoders.controller;

import com.spanishcoders.model.Client;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.UserDTO;
import com.spanishcoders.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<AppointmentDTO> getNextAppointments(Authentication authentication) {
        Set<AppointmentDTO> nextAppointments = userService.getNextAppointments(authentication).stream().map(appointment -> new AppointmentDTO(appointment)).collect(Collectors.toSet());
        return nextAppointments;
    }

    @RequestMapping(value = "register"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Client registerClient(Authentication authentication
            , @RequestBody UserDTO userDTO) {

        Client client = userService.registerClient(authentication
                , userDTO.getUsername()
                , userDTO.getPassword()
                , userDTO.getName()
                , userDTO.getPhone());

        return client;
    }

}
