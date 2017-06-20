package com.spanishcoders.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spanishcoders.appointment.AppointmentDTO;
import com.spanishcoders.appointment.AppointmentStatus;
import com.spanishcoders.work.WorkDTO;
import com.spanishcoders.workingday.schedule.ScheduleDTO;

public class AppointmentTests extends IntegrationTests {

	public static final String APPOINTMENT_URL = "/appointment";

	private HeadersTestRestTemplate<AppointmentDTO> client;
	private final ParameterizedTypeReference<AppointmentDTO> typeRef = new ParameterizedTypeReference<AppointmentDTO>() {
	};
	private HeadersTestRestTemplate<String> errorClient;
	private final ParameterizedTypeReference<String> errorTypeRef = new ParameterizedTypeReference<String>() {
	};
	private String clientAuth;
	private String workerAuth;

	@Before
	public void setUp() {
		client = new HeadersTestRestTemplate<>(testRestTemplate);
		errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
		integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
		clientAuth = loginAsClient();
		workerAuth = loginAsWorker();
	}

	@Test
	public void getAppointmentWithoutAuthorization() {
		final ResponseEntity<String> response = errorClient.getResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				"", errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void getAppointmentWithInvalidWork() throws JsonProcessingException {
		final Set<WorkDTO> works = integrationDataFactory.getWorks(clientAuth);
		final int maxWorkId = works.stream().mapToInt(workDTO -> workDTO.getId()).max().getAsInt();
		final WorkDTO work = new WorkDTO();
		work.setId(maxWorkId + 1);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().addAll(works.stream().map(workId -> workId.getId()).collect(Collectors.toSet()));
		final ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				clientAuth, toJSON(appointmentDTO), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void getAppointmentWithInvalidBlock() throws JsonProcessingException {
		final Set<WorkDTO> works = integrationDataFactory.getWorks(clientAuth);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(works.iterator().next().getId());
		appointmentDTO.getBlocks().add(-1);
		final ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				clientAuth, toJSON(appointmentDTO), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void getAppointmentWithoutEnoughBlocks() throws JsonProcessingException {
		final Set<WorkDTO> works = integrationDataFactory.getWorks(workerAuth);
		Set<ScheduleDTO> schedule = integrationDataFactory.getSchedule(workerAuth, LocalDate.now()).stream()
				.filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
		while (schedule.size() < 1) {
			schedule = integrationDataFactory.getSchedule(workerAuth, LocalDate.now().plusDays(1)).stream()
					.filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
		}
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
		appointmentDTO.getBlocks()
				.add(schedule.stream().findFirst().orElseThrow(IllegalStateException::new).getBlockId());
		final ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				workerAuth, toJSON(appointmentDTO), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void getAppointmentWithTooManyBlocks() throws JsonProcessingException {
		final WorkDTO work = integrationDataFactory.getWorks(clientAuth).stream().findFirst()
				.orElseThrow(IllegalStateException::new);
		Set<ScheduleDTO> schedule = integrationDataFactory.getSchedule(clientAuth, LocalDate.now()).stream()
				.filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
		while (schedule.size() < 2) {
			schedule = integrationDataFactory.getSchedule(clientAuth, LocalDate.now().plusDays(1)).stream()
					.filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
		}
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(work.getId());
		appointmentDTO.getBlocks()
				.addAll(schedule.stream().map(ScheduleDTO -> ScheduleDTO.getBlockId()).collect(Collectors.toSet()));
		final ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				clientAuth, toJSON(appointmentDTO), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void getAppointmentWithOneWorkAsClient() {
		final AppointmentDTO appointmentDTO = confirmAppointmentWithOneWork(clientAuth);
		cancelAppointment(workerAuth, appointmentDTO);
	}

	@Test
	public void getAppointmentWithManyWorksAsClient() {
		final AppointmentDTO appointmentDTO = getAppointmentWithManyWorks(clientAuth);
		cancelAppointment(workerAuth, appointmentDTO);
	}

	@Test
	public void getAppointmentWithOneWorkAsAdmin() {
		final AppointmentDTO appointmentDTO = confirmAppointmentWithOneWork(workerAuth);
		cancelAppointment(workerAuth, appointmentDTO);
	}

	@Test
	public void getAppointmentWithManyWorksAsAdmin() {
		final AppointmentDTO appointmentDTO = getAppointmentWithManyWorks(workerAuth);
		cancelAppointment(workerAuth, appointmentDTO);
	}

	@Test
	public void cancelInvalidAppointment() throws JsonProcessingException {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		final ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				clientAuth, toJSON(appointmentDTO), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void cancelAdminAppointmentAsAdmin() {
		cancelAppointment(workerAuth);
	}

	@Test
	public void cancelAdminAppointmentAsClient() throws JsonProcessingException {
		final AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(workerAuth);
		final ResponseEntity<String> response = errorClient.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				clientAuth, toJSON(toBeCancelled), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void cancelClientAppointmentAsClient() {
		final AppointmentDTO appointmentInTwoDays = integrationDataFactory.getAppointment(clientAuth,
				LocalDate.now().plusDays(2));
		cancelAppointment(clientAuth, appointmentInTwoDays);
	}

	@Test
	public void cancelClientAppointmentAsAdmin() {
		final AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(clientAuth);
		final AppointmentDTO appointment = new AppointmentDTO(toBeCancelled.getId(), null, null, null, null, null,
				AppointmentStatus.CANCELLED, null);
		final AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, workerAuth, appointment,
				typeRef);
		assertThat(cancelled, notNullValue());
		assertThat(cancelled.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test
	public void cancelAppointmentWithPeriodExpiredAsClient() throws JsonProcessingException {
		final AppointmentDTO toBeCancelled = integrationDataFactory.getAppointment(clientAuth);
		final AppointmentDTO appointment = new AppointmentDTO(toBeCancelled.getId(), null, null, null, null, null,
				AppointmentStatus.CANCELLED, null);
		final ResponseEntity<String> response = errorClient.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL,
				clientAuth, toJSON(appointment), errorTypeRef);
		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void cancelAppointmentWithPeriodExpiredAsAdmin() {
		final AppointmentDTO toBeCancelled = integrationDataFactory.getAppointment(workerAuth);
		final AppointmentDTO appointment = new AppointmentDTO(toBeCancelled.getId(), null, null, null, null, null,
				AppointmentStatus.CANCELLED, null);
		final AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, workerAuth, appointment,
				typeRef);
		assertThat(cancelled, notNullValue());
		assertThat(cancelled.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	private AppointmentDTO confirmAppointmentWithOneWork(String auth) {
		return integrationDataFactory.getAppointment(auth);
	}

	private AppointmentDTO getAppointmentWithManyWorks(String auth) {
		final Set<WorkDTO> works = integrationDataFactory.getWorks(auth);
		Set<ScheduleDTO> schedule = integrationDataFactory.getSchedule(auth, LocalDate.now()).stream()
				.filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
		while (schedule.size() <= works.size()) {
			schedule = integrationDataFactory.getSchedule(auth, LocalDate.now().plusDays(1));
		}
		final List<ScheduleDTO> selectedSchedule = schedule.stream().sorted().limit(1).collect(Collectors.toList());
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
		final int firstBlockId = selectedSchedule.get(0).getBlockId();
		for (int i = 0; i < works.size(); i++) {
			appointmentDTO.getBlocks().add(firstBlockId + i);
		}
		return confirmAppointment(auth, appointmentDTO);
	}

	private AppointmentDTO confirmAppointment(String auth, AppointmentDTO appointmentDTO) {
		final AppointmentDTO appointment = client.postWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO,
				typeRef);
		assertThat(appointment, notNullValue());
		assertThat(appointment.getId(), notNullValue());
		assertThat(appointment.getWorks(), is(appointmentDTO.getWorks()));
		assertThat(appointment.getBlocks(), is(appointmentDTO.getBlocks()));
		return appointment;
	}

	private void cancelAppointment(String auth, AppointmentDTO appointmentDTO) {
		final AppointmentDTO appointment = new AppointmentDTO(appointmentDTO.getId(), null, null, null, null, null,
				AppointmentStatus.CANCELLED, null);
		final AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, auth, appointment, typeRef);
		assertThat(cancelled, notNullValue());
		assertThat(cancelled.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	private void cancelAppointment(String auth) {
		final AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(auth);
		cancelAppointment(auth, toBeCancelled);
	}
}
