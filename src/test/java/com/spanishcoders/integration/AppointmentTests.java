package com.spanishcoders.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.appointment.AppointmentDTO;
import com.spanishcoders.work.Work;
import com.spanishcoders.workingday.ScheduleDTO;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by agustin on 11/08/16.
 */
public class AppointmentTests extends IntegrationTests {

    public static final String APPOINTMENT_URL = "/appointment";

    private HeadersTestRestTemplate<AppointmentDTO> client;
    private ParameterizedTypeReference<AppointmentDTO> typeRef = new ParameterizedTypeReference<AppointmentDTO>() {
    };
    private HeadersTestRestTemplate<String> errorClient;
    private ParameterizedTypeReference<String> errorTypeRef = new ParameterizedTypeReference<String>() {
    };

    @Before
    public void setUp() {
        client = new HeadersTestRestTemplate<>(testRestTemplate);
        errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getAppointmentWithoutAuthorization() {
        ResponseEntity<AppointmentDTO> response = client.getResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, "", typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAppointmentWithInvalidWork() throws JsonProcessingException {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        Work work = works.last();
        work.setId(work.getId() + 1);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(workId -> workId.getId()).collect(Collectors.toSet()));
        ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, toJSON(appointmentDTO), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithInvalidBlock() throws JsonProcessingException {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(works.first().getId());
        appointmentDTO.getBlocks().add(-1);
        ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, toJSON(appointmentDTO), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithoutEnoughBlocks() throws JsonProcessingException {
        String auth = loginAsClient();
        Set<Work> works = integrationDataFactory.getWorks(auth);
        Set<ScheduleDTO> schedule = integrationDataFactory.getSchedule(auth, LocalDate.now()).stream().filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
        while (schedule.size() < 1) {
            schedule = integrationDataFactory.getSchedule(auth, LocalDate.now().plusDays(1)).stream().filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
        }
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
        appointmentDTO.getBlocks().add(schedule.stream().findFirst().orElseThrow(IllegalStateException::new).getBlockId());
        ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, toJSON(appointmentDTO), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private String toJSON(AppointmentDTO appointmentDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(appointmentDTO);
    }

    @Test
    public void getAppointmentWithTooManyBlocks() throws JsonProcessingException {
        String auth = loginAsClient();
        Work work = integrationDataFactory.getWorks(auth).stream().findFirst().orElseThrow(IllegalStateException::new);
        Set<ScheduleDTO> schedule = integrationDataFactory.getSchedule(auth, LocalDate.now()).stream().filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
        while (schedule.size() < 2) {
            schedule = integrationDataFactory.getSchedule(auth, LocalDate.now().plusDays(1)).stream().filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
        }
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(work.getId());
        appointmentDTO.getBlocks().addAll(schedule.stream().map(ScheduleDTO -> ScheduleDTO.getBlockId()).collect(Collectors.toSet()));
        ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, toJSON(appointmentDTO), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


    @Test
    public void getAppointmentWithOneWorkAsClient() {
        String auth = loginAsClient();
        AppointmentDTO appointmentDTO = confirmAppointmentWithOneWork(auth);
        String adminAuth = loginAsAdmin();
        cancelAppointment(adminAuth, appointmentDTO);
    }

    @Test
    public void getAppointmentWithManyWorksAsClient() {
        String auth = loginAsClient();
        AppointmentDTO appointmentDTO = getAppointmentWithManyWorks(auth);
        String adminAuth = loginAsAdmin();
        cancelAppointment(adminAuth, appointmentDTO);
    }

    @Test
    public void getAppointmentWithOneWorkAsAdmin() {
        String auth = loginAsAdmin();
        AppointmentDTO appointmentDTO = confirmAppointmentWithOneWork(auth);
        cancelAppointment(auth, appointmentDTO);
    }

    @Test
    public void getAppointmentWithManyWorksAsAdmin() {
        String auth = loginAsAdmin();
        AppointmentDTO appointmentDTO = getAppointmentWithManyWorks(auth);
        cancelAppointment(auth, appointmentDTO);
    }

    @Test
    public void cancelInvalidAppointment() throws JsonProcessingException {
        String auth = loginAsClient();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        ResponseEntity<String> response = errorClient.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, toJSON(appointmentDTO), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void cancelAdminAppointmentAsAdmin() {
        String auth = loginAsAdmin();
        cancelAppointment(auth);
    }

    @Test
    public void cancelAdminAppointmentAsClient() throws JsonProcessingException {
        String adminAuth = loginAsAdmin();
        AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(adminAuth);
        String clientAuth = loginAsClient();
        ResponseEntity<String> response = errorClient.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, clientAuth, toJSON(toBeCancelled), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void cancelClientAppointmentAsClient() {
        String auth = loginAsClient();
        AppointmentDTO appointmentInTwoDays = integrationDataFactory.getAppointment(auth, LocalDate.now().plusDays(2));
        cancelAppointment(auth, appointmentInTwoDays);
    }


    @Test
    public void cancelClientAppointmentAsAdmin() {
        String clientAuth = loginAsClient();
        AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(clientAuth);
        String adminAuth = loginAsAdmin();
        Appointment appointment = new Appointment();
        appointment.setId(toBeCancelled.getId());
        appointment.cancel();
        AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, adminAuth, new AppointmentDTO(appointment), typeRef);
        assertThat(cancelled, notNullValue());
        assertThat(cancelled.getStatus(), is(1));
    }

    @Test
    public void cancelAppointmentWithPeriodExpiredAsClient() throws JsonProcessingException {
        String auth = loginAsClient();
        AppointmentDTO appointmentDTO = integrationDataFactory.getAppointment(auth);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentDTO.getId());
        appointment.cancel();
        ResponseEntity<String> response = errorClient.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, toJSON(new AppointmentDTO(appointment)), errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void cancelAppointmentWithPeriodExpiredAsAdmin() {
        String auth = loginAsAdmin();
        AppointmentDTO appointmentDTO = integrationDataFactory.getAppointment(auth);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentDTO.getId());
        appointment.cancel();
        AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, auth, new AppointmentDTO(appointment), typeRef);
        assertThat(cancelled, notNullValue());
        assertThat(cancelled.getStatus(), is(1));
    }

    private AppointmentDTO confirmAppointmentWithOneWork(String auth) {
        return integrationDataFactory.getAppointment(auth);
    }

    private AppointmentDTO getAppointmentWithManyWorks(String auth) {
        Set<Work> works = integrationDataFactory.getWorks(auth);
        Set<ScheduleDTO> schedule = integrationDataFactory.getSchedule(auth, LocalDate.now()).stream().filter(scheduleDTO -> scheduleDTO.getAppointmentId() == 0).collect(Collectors.toSet());
        while (schedule.size() <= works.size()) {
            schedule = integrationDataFactory.getSchedule(auth, LocalDate.now().plusDays(1));
        }
        List<ScheduleDTO> selectedSchedule = schedule.stream().sorted().limit(1).collect(Collectors.toList());
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
        int firstBlockId = selectedSchedule.get(0).getBlockId();
        for (int i = 0; i < works.size(); i++) {
            appointmentDTO.getBlocks().add(firstBlockId + i);
        }
        return confirmAppointment(auth, appointmentDTO);
    }

    private AppointmentDTO confirmAppointment(String auth, AppointmentDTO appointmentDTO) {
        AppointmentDTO appointment = client.postWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        System.out.println(appointment);
        assertThat(appointment, notNullValue());
        assertThat(appointment.getId(), notNullValue());
        assertThat(appointment.getWorks(), is(appointmentDTO.getWorks()));
        assertThat(appointment.getBlocks(), is(appointmentDTO.getBlocks()));
        return appointment;
    }

    private void cancelAppointment(String auth, AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment();
        appointment.setId(appointmentDTO.getId());
        appointment.cancel();
        AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, auth, new AppointmentDTO(appointment), typeRef);
        assertThat(cancelled, notNullValue());
        assertThat(cancelled.getStatus(), is(1));
    }

    private void cancelAppointment(String auth) {
        AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(auth);
        cancelAppointment(auth, toBeCancelled);
    }
}
