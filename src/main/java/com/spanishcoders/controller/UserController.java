package com.spanishcoders.controller;

import com.spanishcoders.model.Client;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.ClientDTO;
import com.spanishcoders.model.dto.UserDTO;
import com.spanishcoders.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @RequestMapping(value = "client"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    Client registerClient(Authentication authentication
            , @RequestBody ClientDTO clientDTO) {

        Client client = userService.createClient(authentication, clientDTO);

        return client;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "hairdresser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Hairdresser registerHairdresser(Authentication authentication, @RequestBody UserDTO userDTO) {
        Hairdresser hairdresser = userService.registerHairdresser(authentication, userDTO);
        return hairdresser;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedExceptionExceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

}
