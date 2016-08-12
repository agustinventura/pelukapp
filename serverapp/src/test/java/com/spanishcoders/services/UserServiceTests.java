package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.User;
import com.spanishcoders.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

public class UserServiceTests extends PelukaapUnitTest {

    @MockBean
    private UserRepository userRepository;

    private UserService userService;

    @Before
    public void setUp() {
        //TODO check password encoder (I have no idea what I am doing)
        userService = new UserService(userRepository, new BCryptPasswordEncoder());
    }

    @Test(expected = AccessDeniedException.class)
    public void getNextAppointmentsNullAuthentication() {
        userService.getNextAppointments(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void getNextAppointmentsNonExistingUser() {
        given(userRepository.findByUsername(any(String.class))).willReturn(null);
        userService.getNextAppointments(Mockito.mock(Authentication.class));
    }

    @Test
    public void getNextAppointmentsUserWithoutAppointments() {
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = Mockito.mock(User.class);
        Set<Appointment> userAppointmnets = Sets.newHashSet();
        given(user.getAppointments()).willReturn(userAppointmnets);
        given(userRepository.findByUsername(any(String.class))).willReturn(user);
        Set<Appointment> appointments = userService.getNextAppointments(authentication);
        assertThat(appointments, is(userAppointmnets));
    }

    @Test
    public void getNextAppointmentsUserWithAppointments() {
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = Mockito.mock(User.class);
        Appointment mockAppointment = Mockito.mock(Appointment.class);
        Set<Appointment> userAppointments = Sets.newHashSet(mockAppointment);
        given(user.getAppointments()).willReturn(userAppointments);
        given(userRepository.findByUsername(any(String.class))).willReturn(user);
        Set<Appointment> appointments = userService.getNextAppointments(authentication);
        assertThat(appointments, is(userAppointments));
    }
}