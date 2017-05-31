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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.spanishcoders.appointment.AppointmentDTO;
import com.spanishcoders.user.SignInUserDTO;
import com.spanishcoders.user.client.ClientDTO;

public class UserTests extends IntegrationTests {

	public final static String NEXT_APPOINTMENTS_URL = "/user/appointments/next";

	@Before
	public void setUp() {
		integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
	}

	@Test
	public void clientLogin() {
		final SignInUserDTO client = login(CLIENT_USERNAME, CLIENT_PASSWORD).getBody();
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
		final SignInUserDTO admin = login(ADMIN_USERNAME, ADMIN_PASSWORD).getBody();
		assertThat(admin.getUsername(), is(ADMIN_USERNAME));
	}

	@Test
	public void registerUser() {

		final String username = "usuario1234";
		final String password = "usuario1234";
		final String name = "usuario";
		final String phone = "666666666";

		final ResponseEntity<ClientDTO> registrationResponse = register(username, password, phone, name);
		assertThat(registrationResponse.getStatusCode(), is(HttpStatus.OK));

		final ResponseEntity<SignInUserDTO> loginResponse = login(username, password);
		assertThat(loginResponse.getStatusCode(), is(HttpStatus.OK));
	}

	private ResponseEntity<ClientDTO> register(String username, String password, String phone, String name) {
		final ClientDTO clientDTO = new ClientDTO();
		clientDTO.setUsername(username);
		clientDTO.setPassword(password);
		clientDTO.setName(name);
		clientDTO.setPhone(phone);
		final HttpEntity<ClientDTO> request = new HttpEntity<>(clientDTO);
		final ResponseEntity<ClientDTO> response = testRestTemplate.postForEntity(REGISTER_CLIENT_URL, request,
				ClientDTO.class);
		return response;
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
