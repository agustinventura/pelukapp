package com.spanishcoders.integration;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.spanishcoders.user.hairdresser.HairdresserDTO;
import com.spanishcoders.workingday.schedule.HairdresserScheduleDTO;
import com.spanishcoders.workingday.schedule.ScheduleDTO;

public class HairdresserTests extends IntegrationTests {

	public static final String TODAY_SCHEDULE_URL = "/hairdresser/schedule/today";
	public static final String DAY_SCHEDULE_URL = "/hairdresser/schedule/";
	ParameterizedTypeReference<Object> errorTypeRef = new ParameterizedTypeReference<Object>() {
	};
	private HeadersTestRestTemplate<List<HairdresserScheduleDTO>> hairdresserScheduleClient;
	private final ParameterizedTypeReference<List<HairdresserScheduleDTO>> hairdresserScheduleTypeRef = new ParameterizedTypeReference<List<HairdresserScheduleDTO>>() {
	};
	private HeadersTestRestTemplate<Object> errorClient;

	@Before
	public void setUp() {
		hairdresserScheduleClient = new HeadersTestRestTemplate<>(testRestTemplate);
		errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
		integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
	}

	@Test
	public void getScheduleWithoutAuthorization() {
		final ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL,
				"", errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void getScheduleInvalidDay() {
		final String clientAuth = loginAsClient();
		final ResponseEntity<Object> response = errorClient
				.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL + "/2016-xx-01", clientAuth, errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void getScheduleNonWorkingDay() {
		final String authHeader = loginAsClient();
		final List<HairdresserScheduleDTO> schedule = hairdresserScheduleClient
				.getWithAuthorizationHeader(
						DAY_SCHEDULE_URL + LocalDate.of(LocalDate.now().getYear(), 01, 01)
								.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/",
						authHeader, hairdresserScheduleTypeRef);
		assertThat(schedule, is(not(empty())));
		final HairdresserScheduleDTO hairdresserSchedule = schedule.get(0);
		final HairdresserDTO hairdresser = hairdresserSchedule.getHairdresser();
		assertThat(hairdresser, notNullValue());
		final Set<ScheduleDTO> scheduleBlocks = hairdresserSchedule.getSchedule();
		assertThat(scheduleBlocks.size(), is(0));
	}

	@Test
	public void getScheduleAsClient() {
		final String authHeader = loginAsClient();
		final List<HairdresserScheduleDTO> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(
				DAY_SCHEDULE_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/", authHeader,
				hairdresserScheduleTypeRef);
		assertThat(schedule, is(not(empty())));
		final HairdresserScheduleDTO hairdresserSchedule = schedule.get(0);
		final HairdresserDTO hairdresser = hairdresserSchedule.getHairdresser();
		assertThat(hairdresser, notNullValue());
		final Set<ScheduleDTO> scheduleBlocks = hairdresserSchedule.getSchedule();
		assertThat(scheduleBlocks.size(), is(notNullValue()));
	}

	@Test
	public void getScheduleAsHairdresser() {
		final String authHeader = loginAsWorker();
		final List<HairdresserScheduleDTO> hairdresserSchedule = hairdresserScheduleClient.getWithAuthorizationHeader(
				DAY_SCHEDULE_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/", authHeader,
				hairdresserScheduleTypeRef);
		assertThat(hairdresserSchedule, is(not(empty())));
		final HairdresserScheduleDTO hairdresserAvailableBlocks = hairdresserSchedule.get(0);
		final HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
		assertThat(hairdresser, notNullValue());
		final Set<ScheduleDTO> scheduleBlocks = hairdresserAvailableBlocks.getSchedule();
		assertThat(scheduleBlocks.size(), is(notNullValue()));
	}

	@Test
	public void getTodayScheduleWithoutAuthorization() {
		final ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(TODAY_SCHEDULE_URL,
				"", errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void getTodayScheduleAsClient() {
		final String clientAuth = loginAsClient();
		final List<HairdresserScheduleDTO> schedule = hairdresserScheduleClient
				.getWithAuthorizationHeader(TODAY_SCHEDULE_URL, clientAuth, hairdresserScheduleTypeRef);
		assertThat(schedule, is(not(empty())));
		assertThat(schedule.stream().filter(hairdresserSchedule -> getScheduleWithOtherClients(hairdresserSchedule))
				.collect(Collectors.toSet()), is(empty()));
	}

	@Test
	public void getTodayScheduleAsAdmin() {
		final String adminAuth = loginAsWorker();
		final List<HairdresserScheduleDTO> schedule = hairdresserScheduleClient
				.getWithAuthorizationHeader(TODAY_SCHEDULE_URL, adminAuth, hairdresserScheduleTypeRef);
		assertThat(schedule, is(not(empty())));
	}

	@Test
	public void getDayScheduleWithoutAuthorization() {
		final ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL,
				"", errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void getDayScheduleInvalidDay() {
		final String adminAuth = loginAsWorker();
		final ResponseEntity<Object> response = errorClient
				.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL + "2016-xy-01", adminAuth, errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void getDayScheduleAsClient() {
		final String clientAuth = loginAsClient();
		final List<HairdresserScheduleDTO> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(
				DAY_SCHEDULE_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), clientAuth,
				hairdresserScheduleTypeRef);
		assertThat(schedule, is(not(empty())));
		assertThat(schedule.stream().filter(hairdresserSchedule -> getScheduleWithOtherClients(hairdresserSchedule))
				.collect(Collectors.toSet()), is(empty()));
	}

	private boolean getScheduleWithOtherClients(HairdresserScheduleDTO hairdresserSchedule) {
		return hairdresserSchedule.getSchedule().stream()
				.anyMatch(scheduleDTO -> (!StringUtils.isEmpty(scheduleDTO.getClient())
						&& !scheduleDTO.getClient().equals(CLIENT_USERNAME)));
	}

	@Test
	public void getDayScheduleAsAdmin() {
		final String adminAuth = loginAsWorker();
		final List<HairdresserScheduleDTO> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(
				DAY_SCHEDULE_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), adminAuth,
				hairdresserScheduleTypeRef);
		assertThat(schedule, is(not(empty())));
	}
}
