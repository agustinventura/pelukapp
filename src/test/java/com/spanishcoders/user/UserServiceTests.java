package com.spanishcoders.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.appointment.AppointmentService;

public class UserServiceTests extends PelukaapUnitTest {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private AppointmentService appointmentService;

	private UserService userService;

	@Before
	public void setUp() {
		userService = new UserService(userRepository, appointmentService, passwordEncoder);
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
		final Authentication authentication = Mockito.mock(Authentication.class);
		final AppUser user = Mockito.mock(AppUser.class);
		final Set<Appointment> userAppointmnets = Sets.newHashSet();
		given(user.getAppointments()).willReturn(userAppointmnets);
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
		final Set<Appointment> appointments = userService.getNextAppointments(authentication);
		assertThat(appointments, is(userAppointmnets));
	}

	@Test
	public void getNextAppointmentsUserWithAppointments() {
		final Authentication authentication = Mockito.mock(Authentication.class);
		final AppUser user = Mockito.mock(AppUser.class);
		final Appointment mockAppointment = Mockito.mock(Appointment.class);
		final Set<Appointment> userAppointments = Sets.newHashSet(mockAppointment);
		given(appointmentService.getNextAppointments(any(AppUser.class))).willReturn(userAppointments);
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
		final Set<Appointment> appointments = userService.getNextAppointments(authentication);
		assertThat(appointments, is(userAppointments));
	}
}