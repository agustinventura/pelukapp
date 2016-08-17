package com.spanishcoders.integration;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.BlockDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
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

    @Before
    public void setUp() {
        client = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getAppointmentWithoutAuthorization() {
        ResponseEntity<AppointmentDTO> response = client.getResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, "", typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAppointmentWithInvalidWork() {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        Work work = works.last();
        work.setId(work.getId() + 1);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(workId -> workId.getId()).collect(Collectors.toSet()));
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithInvalidBlock() {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(works.first().getId());
        appointmentDTO.getBlocks().add(-1);
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithoutEnoughBlocks() {
        String auth = loginAsClient();
        Set<Work> works = integrationDataFactory.getWorks(auth);
        TreeSet<BlockDTO> blocks = (TreeSet<BlockDTO>) integrationDataFactory.getBlocks(auth, works);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
        appointmentDTO.getBlocks().add(blocks.first().getId());
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithTooManyBlocks() {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        Set<BlockDTO> blocks = integrationDataFactory.getBlocks(auth, works);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(works.first().getId());
        appointmentDTO.getBlocks().addAll(blocks.stream().map(blockDTO -> blockDTO.getId()).collect(Collectors.toSet()));
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithOneWorkAsClient() {
        String auth = loginAsClient();
        confirmAppointmentWithOneWork(auth);
    }

    @Test
    public void getAppointmentWithManyWorksAsClient() {
        String auth = loginAsClient();
        getAppointmentWithManyWorks(auth);
    }

    @Test
    public void getAppointmentWithOneWorkAsAdmin() {
        String auth = loginAsAdmin();
        confirmAppointmentWithOneWork(auth);
    }

    @Test
    public void getAppointmentWithManyWorksAsAdmin() {
        String auth = loginAsAdmin();
        getAppointmentWithManyWorks(auth);
    }

    @Test
    public void cancelInvalidAppointment() {
        String auth = loginAsClient();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        ResponseEntity<AppointmentDTO> response = client.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void cancelAdminAppointmentAsAdmin() {
        String auth = loginAsAdmin();
        cancelAppointment(auth);
    }

    @Test
    public void cancelAdminAppointmentAsClient() {
        String adminAuth = loginAsAdmin();
        AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(adminAuth);
        String clientAuth = loginAsClient();
        ResponseEntity<AppointmentDTO> response = client.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, clientAuth, toBeCancelled, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void cancelClientAppointmentAsClient() {
        String auth = loginAsClient();
        cancelAppointment(auth);
    }

    @Test
    public void cancelClientAppointmentAsAdmin() {
        String clientAuth = loginAsClient();
        AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(clientAuth);
        String adminAuth = loginAsAdmin();
        AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, adminAuth, toBeCancelled, typeRef);
        assertThat(cancelled, notNullValue());
        assertThat(cancelled.getStatus(), is(1));
    }

    @Test
    public void cancelAppointmentWithPeriodExpiredAsClient() {
        String auth = loginAsClient();
        AppointmentDTO appointmentDTO = this.getAppointmentForToday(auth);
        appointmentDTO = confirmAppointment(auth, appointmentDTO);
        ResponseEntity<AppointmentDTO> response = client.putResponseEntityWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void cancelAppointmentWithPeriodExpiredAsAdmin() {
        String auth = loginAsAdmin();
        AppointmentDTO appointmentDTO = this.getAppointmentForToday(auth);
        appointmentDTO = confirmAppointment(auth, appointmentDTO);
        AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(cancelled, notNullValue());
        assertThat(cancelled.getStatus(), is(1));
    }

    private AppointmentDTO confirmAppointmentWithOneWork(String auth) {
        return integrationDataFactory.getAppointment(auth);
    }

    private void getAppointmentWithManyWorks(String auth) {
        Set<Work> works = integrationDataFactory.getWorks(auth);
        Set<BlockDTO> blocks = integrationDataFactory.getBlocks(auth, works).stream().skip(5).limit(works.size()).collect(Collectors.toSet());
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
        appointmentDTO.getBlocks().addAll(blocks.stream().map(blockDTO -> blockDTO.getId()).collect(Collectors.toSet()));
        confirmAppointment(auth, appointmentDTO);
    }

    private AppointmentDTO confirmAppointment(String auth, AppointmentDTO appointmentDTO) {
        AppointmentDTO appointment = client.postWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(appointment, notNullValue());
        assertThat(appointment.getId(), notNullValue());
        assertThat(appointment.getWorks(), is(appointmentDTO.getWorks()));
        assertThat(appointment.getBlocks(), is(appointmentDTO.getBlocks()));
        return appointment;
    }

    private void cancelAppointment(String auth) {
        AppointmentDTO toBeCancelled = confirmAppointmentWithOneWork(auth);
        AppointmentDTO cancelled = client.putWithAuthorizationHeader(APPOINTMENT_URL, auth, toBeCancelled, typeRef);
        assertThat(cancelled, notNullValue());
        assertThat(cancelled.getStatus(), is(1));
    }

    private AppointmentDTO getAppointmentForToday(String auth) {
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        Work work = works.first();
        TreeSet<BlockDTO> blocks = (TreeSet<BlockDTO>) integrationDataFactory.getBlocks(auth, works);
        BlockDTO block = blocks.first();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(work.getId());
        appointmentDTO.getBlocks().add(block.getId());
        LocalDate today = LocalDate.now();
        return appointmentDTO;
    }
}
