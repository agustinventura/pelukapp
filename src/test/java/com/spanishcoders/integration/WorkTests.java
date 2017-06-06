package com.spanishcoders.integration;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.spanishcoders.work.WorkDTO;
import com.spanishcoders.work.WorkKind;

public class WorkTests extends IntegrationTests {

	public static final String WORKS_URL = "/works";

	private HeadersTestRestTemplate<Set<WorkDTO>> client;
	private final ParameterizedTypeReference<Set<WorkDTO>> typeRef = new ParameterizedTypeReference<Set<WorkDTO>>() {
	};

	@Before
	public void setUp() {
		client = new HeadersTestRestTemplate<>(testRestTemplate);
	}

	@Test
	public void getWorksWithoutAuthorization() {
		final HeadersTestRestTemplate<WorkDTO> client = new HeadersTestRestTemplate<>(testRestTemplate);
		final ParameterizedTypeReference<WorkDTO> typeRef = new ParameterizedTypeReference<WorkDTO>() {
		};
		final ResponseEntity<WorkDTO> result = client.getResponseEntityWithAuthorizationHeader(WORKS_URL, "", typeRef);
		assertThat(result.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void getWorksAsClient() {
		final String authHeader = loginAsClient();
		final Set<WorkDTO> works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, typeRef);
		assertThat(works, not(empty()));
		for (final WorkDTO work : works) {
			assertThat(work.getWorkKind(), is(WorkKind.PUBLIC));
		}
	}

	@Test
	public void getWorksAsAdmin() {
		final String authHeader = loginAsWorker();
		final Set<WorkDTO> works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, typeRef);
		assertThat(works, not(empty()));
	}
}
