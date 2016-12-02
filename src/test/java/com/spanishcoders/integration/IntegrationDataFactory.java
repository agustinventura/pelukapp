package com.spanishcoders.integration;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.HairdresserSchedule;
import com.spanishcoders.model.dto.ScheduleDTO;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.spanishcoders.integration.AppointmentTests.APPOINTMENT_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by agustin on 11/08/16.
 */
public class IntegrationDataFactory {

    private TestRestTemplate testRestTemplate;

    public IntegrationDataFactory(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    public Set<Work> getWorks(String auth) {
        HeadersTestRestTemplate<Set<Work>> worksClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<Set<Work>> worksTypeRef = new ParameterizedTypeReference<Set<Work>>() {
        };
        return Sets.newTreeSet(worksClient.getWithAuthorizationHeader(WorkTests.WORKS_URL, auth, worksTypeRef));
    }

    public Set<ScheduleDTO> getSchedule(String auth, LocalDate date) {
        HeadersTestRestTemplate<List<HairdresserSchedule>> scheduleClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<List<HairdresserSchedule>> scheduleTypeRef = new ParameterizedTypeReference<List<HairdresserSchedule>>() {
        };
        String scheduleUrl = HairdresserTests.DAY_SCHEDULE_URL;
        if (date != null) {
            scheduleUrl += date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/";
        }
        List<HairdresserSchedule> hairdresserAvailableBlocks = scheduleClient.getWithAuthorizationHeader(scheduleUrl, auth, scheduleTypeRef);
        return Sets.newTreeSet(hairdresserAvailableBlocks.get(0).getSchedule());
    }

    public AppointmentDTO getAppointment(String auth) {
        return getAppointment(auth, LocalDate.now());
    }

    public AppointmentDTO getAppointment(String auth, LocalDate date) {
        Work work = this.getWorks(auth).stream().findFirst().orElseThrow(IllegalStateException::new);
        Set<ScheduleDTO> scheduleBlocks = this.getSchedule(auth, date).stream().filter(scheduleDTO -> hasOneAvailableBlock(scheduleDTO)).collect(Collectors.toSet());
        while (scheduleBlocks.size() == 0) {
            scheduleBlocks = this.getSchedule(auth, date.plusDays(1));
        }
        ScheduleDTO schedule = scheduleBlocks.stream().sorted().findFirst().orElseThrow(IllegalStateException::new);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(work.getId());
        appointmentDTO.getBlocks().add(schedule.getBlockId());
        HeadersTestRestTemplate<AppointmentDTO> appointmentsClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<AppointmentDTO> appointmentsTypeRef = new ParameterizedTypeReference<AppointmentDTO>() {
        };
        AppointmentDTO confirmed = appointmentsClient.postWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, appointmentsTypeRef);
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
