package com.spanishcoders.integration;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.BlockDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by agustin on 11/08/16.
 */
public class AppointmentTests extends IntegrationTests {

    public static final String NEW_APPOINTMENT_URL = "/appointment/new";

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
        ResponseEntity<AppointmentDTO> response = client.getResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, "", typeRef);
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
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithInvalidBlock() {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(works.first().getId());
        appointmentDTO.getBlocks().add(-1);
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, auth, appointmentDTO, typeRef);
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
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, auth, appointmentDTO, typeRef);
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
        ResponseEntity<AppointmentDTO> response = client.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithOneWorkAsClient() {
        String auth = loginAsClient();
        confirmAppointmentWithOneWork(auth);
    }

    private void confirmAppointmentWithOneWork(String auth) {
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        Work work = works.first();
        TreeSet<BlockDTO> blocks = (TreeSet<BlockDTO>) integrationDataFactory.getBlocks(auth, works);
        BlockDTO block = blocks.first();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(work.getId());
        appointmentDTO.getBlocks().add(block.getId());
        AppointmentDTO appointment = client.postWithAuthorizationHeader(NEW_APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(appointment, notNullValue());
        assertThat(appointment.getId(), notNullValue());
        assertThat(appointment.getWorks(), hasItem(work.getId()));
        assertThat(appointment.getBlocks(), hasItem(block.getId()));
    }

    @Test
    public void getAppointmentWithManyWorksAsClient() {
        String auth = loginAsClient();
        confirmAppointmentWithManyWorks(auth);
    }

    private void confirmAppointmentWithManyWorks(String auth) {
        Set<Work> works = integrationDataFactory.getWorks(auth);
        Set<BlockDTO> blocks = integrationDataFactory.getBlocks(auth, works).stream().limit(works.size()).collect(Collectors.toSet());
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(works.stream().map(work -> work.getId()).collect(Collectors.toSet()));
        appointmentDTO.getBlocks().addAll(blocks.stream().map(blockDTO -> blockDTO.getId()).collect(Collectors.toSet()));
        AppointmentDTO appointment = client.postWithAuthorizationHeader(NEW_APPOINTMENT_URL, auth, appointmentDTO, typeRef);
        assertThat(appointment, notNullValue());
        assertThat(appointment.getId(), notNullValue());
        Set<Integer> worksId = works.stream().map(work -> work.getId()).collect(Collectors.toSet());
        assertThat(appointment.getWorks().equals(worksId), is(true));
        Set<Integer> blocksId = blocks.stream().map(block -> block.getId()).collect(Collectors.toSet());
        assertThat(appointment.getBlocks().equals(blocksId), is(true));
    }

    @Test
    public void getAppointmentWithOneWorkAsAdmin() {
        String auth = loginAsAdmin();
        confirmAppointmentWithOneWork(auth);
    }

    @Test
    public void getAppointmentWithManyWorksAsAdmin() {
        String auth = loginAsAdmin();
        confirmAppointmentWithManyWorks(auth);
    }
}
