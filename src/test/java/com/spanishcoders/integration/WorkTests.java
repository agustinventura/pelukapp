package com.spanishcoders.integration;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

import java.time.Duration;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spanishcoders.work.WorkDTO;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkStatus;

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

	@Test
	public void createWorkAsClient() throws JsonProcessingException {
		final String clientAuth = loginAsClient();
		final HeadersTestRestTemplate<String> client = new HeadersTestRestTemplate<>(testRestTemplate);
		final ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {
		};
		final ResponseEntity<String> result = client.postResponseEntityWithAuthorizationHeader(WORKS_URL, clientAuth,
				toJSON(new WorkDTO()), typeRef);
		assertThat(result.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void createWorkAsAdmin() {
		final String workerAuth = loginAsWorker();
		final HeadersTestRestTemplate<WorkDTO> client = new HeadersTestRestTemplate<>(testRestTemplate);
		final ParameterizedTypeReference<WorkDTO> typeRef = new ParameterizedTypeReference<WorkDTO>() {
		};
		final WorkDTO dto = new WorkDTO();
		dto.setDuration(Duration.ofMinutes(30L));
		dto.setName("test");
		dto.setWorkKind(WorkKind.PUBLIC);
		dto.setWorkStatus(WorkStatus.ENABLED);
		final WorkDTO result = client.postWithAuthorizationHeader(WORKS_URL, workerAuth, dto, typeRef);
		assertThat(result, is(dto));
	}

	@Test
	public void createEmptyWork() {
		final String workerAuth = loginAsWorker();
		final HeadersTestRestTemplate<WorkDTO> client = new HeadersTestRestTemplate<>(testRestTemplate);
		final ParameterizedTypeReference<WorkDTO> typeRef = new ParameterizedTypeReference<WorkDTO>() {
		};
		final ResponseEntity<WorkDTO> result = client.postResponseEntityWithAuthorizationHeader(WORKS_URL, workerAuth,
				new WorkDTO(), typeRef);
		assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}
}
