package com.spanishcoders.integration;

import static com.spanishcoders.integration.AppointmentTests.APPOINTMENT_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.google.common.collect.Sets;
import com.spanishcoders.appointment.AppointmentDTO;
import com.spanishcoders.work.WorkDTO;
import com.spanishcoders.workingday.schedule.HairdresserScheduleDTO;
import com.spanishcoders.workingday.schedule.ScheduleDTO;

/**
 * Created by agustin on 11/08/16.
 */
public class IntegrationDataFactory {

	private final TestRestTemplate testRestTemplate;

	public IntegrationDataFactory(TestRestTemplate testRestTemplate) {
		this.testRestTemplate = testRestTemplate;
	}

	public Set<WorkDTO> getWorks(String auth) {
		final HeadersTestRestTemplate<Set<WorkDTO>> worksClient = new HeadersTestRestTemplate<>(testRestTemplate);
		final ParameterizedTypeReference<Set<WorkDTO>> worksTypeRef = new ParameterizedTypeReference<Set<WorkDTO>>() {
		};
		return worksClient.getWithAuthorizationHeader(WorkTests.WORKS_URL, auth, worksTypeRef);
	}

	public Set<ScheduleDTO> getSchedule(String auth, LocalDate date) {
		final HeadersTestRestTemplate<List<HairdresserScheduleDTO>> scheduleClient = new HeadersTestRestTemplate<>(
				testRestTemplate);
		final ParameterizedTypeReference<List<HairdresserScheduleDTO>> scheduleTypeRef = new ParameterizedTypeReference<List<HairdresserScheduleDTO>>() {
		};
		String scheduleUrl = HairdresserTests.DAY_SCHEDULE_URL;
		if (date != null) {
			scheduleUrl += date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/";
		}
		final List<HairdresserScheduleDTO> hairdresserAvailableBlocks = scheduleClient
				.getWithAuthorizationHeader(scheduleUrl, auth, scheduleTypeRef);
		return Sets.newTreeSet(hairdresserAvailableBlocks.get(0).getSchedule());
	}

	public AppointmentDTO getAppointment(String auth) {
		return getAppointment(auth, LocalDate.now());
	}

	public AppointmentDTO getAppointment(String auth, LocalDate date) {
		final WorkDTO work = this.getWorks(auth).stream().findFirst().orElseThrow(IllegalStateException::new);
		Set<ScheduleDTO> scheduleBlocks = this.getSchedule(auth, date).stream()
				.filter(scheduleDTO -> hasOneAvailableBlock(scheduleDTO)).collect(Collectors.toSet());
		while (scheduleBlocks.size() == 0) {
			scheduleBlocks = this.getSchedule(auth, date.plusDays(1));
		}
		final ScheduleDTO schedule = scheduleBlocks.stream().sorted().findFirst()
				.orElseThrow(IllegalStateException::new);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(work.getId());
		appointmentDTO.getBlocks().add(schedule.getBlockId());
		final HeadersTestRestTemplate<AppointmentDTO> appointmentsClient = new HeadersTestRestTemplate<>(
				testRestTemplate);
		final ParameterizedTypeReference<AppointmentDTO> appointmentsTypeRef = new ParameterizedTypeReference<AppointmentDTO>() {
		};
		final AppointmentDTO confirmed = appointmentsClient.postWithAuthorizationHeader(APPOINTMENT_URL, auth,
				appointmentDTO, appointmentsTypeRef);
		assertThat(confirmed, notNullValue());
		assertThat(confirmed.getId(), notNullValue());
		assertThat(confirmed.getWorks(), is(appointmentDTO.getWorks()));
		assertThat(confirmed.getBlocks(), is(appointmentDTO.getBlocks()));
		return confirmed;
	}

	private boolean hasOneAvailableBlock(ScheduleDTO scheduleDTO) {
		return scheduleDTO.getAppointmentId() == 0;
	}
}
