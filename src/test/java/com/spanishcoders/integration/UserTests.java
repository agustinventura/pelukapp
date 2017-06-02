package com.spanishcoders.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.spanishcoders.user.UserDTO;

public class UserTests extends IntegrationTests {

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
}
