package com.spanishcoders.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.spanishcoders.user.UserDTO;

/**
 * Created by agustin on 8/08/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
public abstract class IntegrationTests {

	public static final String LOGIN_URL = "/login";
	public static final String REGISTER_CLIENT_URL = "/user/client";
	public static final String AUTH_HEADER = "X-AUTH-TOKEN";
	public static final String CLIENT_USERNAME = "client";
	public static final String CLIENT_PASSWORD = "client";
	public static final String ADMIN_USERNAME = "admin";
	public static final String ADMIN_PASSWORD = "admin";

	@Autowired
	protected TestRestTemplate testRestTemplate;

	protected IntegrationDataFactory integrationDataFactory;

	protected String loginAsClient() {
		final ResponseEntity<UserDTO> response = login(CLIENT_USERNAME, CLIENT_PASSWORD);
		final String authToken = getAuthHeader(response);
		return authToken;
	}

	protected String loginAsAdmin() {
		final ResponseEntity<UserDTO> response = login(ADMIN_USERNAME, ADMIN_PASSWORD);
		final String authToken = getAuthHeader(response);
		return authToken;
	}

	protected ResponseEntity<UserDTO> login(String username, String password) {
		final HttpEntity<UserDTO> request = getRequestWithUserDTO(username, password);
		final ResponseEntity<UserDTO> response = testRestTemplate.postForEntity(LOGIN_URL, request, UserDTO.class);
		return response;
	}

	protected ResponseEntity<String> loginForError(String username, String password) {
		final HttpEntity<UserDTO> request = getRequestWithUserDTO(username, password);
		final ResponseEntity<String> response = testRestTemplate.postForEntity(LOGIN_URL, request, String.class);
		return response;
	}

	private HttpEntity<UserDTO> getRequestWithUserDTO(String username, String password) {
		final UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		return new HttpEntity<>(userDTO);
	}

	private String getAuthHeader(ResponseEntity<UserDTO> response) {
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		final HttpHeaders headers = response.getHeaders();
		assertThat(headers, notNullValue());
		assertThat(headers.containsKey(AUTH_HEADER), is(true));
		final String authToken = headers.get(AUTH_HEADER).get(0);
		assertThat(authToken, notNullValue());
		return authToken;
	}
}
