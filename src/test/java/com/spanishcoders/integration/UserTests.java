package com.spanishcoders.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.spanishcoders.appointment.AppointmentDTO;
import com.spanishcoders.user.UserDTO;

public class UserTests extends IntegrationTests {

	public final static String NEXT_APPOINTMENTS_URL = "/user/appointments/next";

	@Before
	public void setUp() {
		integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
	}

	@Test
	public void clientLogin() {
		final UserDTO client = login(CLIENT_USERNAME, CLIENT_PASSWORD).getBody();
		assertThat(client.getUsername(), is(CLIENT_USERNAME));
	}

	@Test
	public void loginWithWrongPassword() {
		final ResponseEntity<String> response = loginForError(CLIENT_USERNAME, CLIENT_PASSWORD + "s");
		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void loginWithWrongUsername() {
		final ResponseEntity<String> response = loginForError(CLIENT_USERNAME + "s", CLIENT_PASSWORD);
		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void adminLogin() {
		final UserDTO admin = login(ADMIN_USERNAME, ADMIN_PASSWORD).getBody();
		assertThat(admin.getUsername(), is(ADMIN_USERNAME));
	}

	@Test
	public void getNextAppointments() {
		final String auth = loginAsClient();
		final AppointmentDTO appointment = integrationDataFactory.getAppointment(auth);
		final HeadersTestRestTemplate<Set<AppointmentDTO>> appointmentClient = new HeadersTestRestTemplate<>(
				testRestTemplate);
		final ParameterizedTypeReference<Set<AppointmentDTO>> appointmentTypeRef = new ParameterizedTypeReference<Set<AppointmentDTO>>() {
		};
		final Set<AppointmentDTO> nextAppointments = appointmentClient.getWithAuthorizationHeader(NEXT_APPOINTMENTS_URL,
				auth, appointmentTypeRef);
		assertThat(nextAppointments, not(empty()));
		assertThat(nextAppointments, hasItem(appointment));
	}
}
