package com.spanishcoders.integration;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.BlockDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by agustin on 11/08/16.
 */
public class AppointmentTests extends IntegrationTests {

    public static final String NEW_APPOINTMENT_URL = "/appointment/new/";

    private HeadersTestRestTemplate<AppointmentDTO> client;
    private ParameterizedTypeReference<AppointmentDTO> typeRef = new ParameterizedTypeReference<AppointmentDTO>() {
    };

    @Before
    public void setUp() {
        client = new HeadersTestRestTemplate<>(testRestTemplate);
        errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getAppointmentWithoutAuthorization() {
        ResponseEntity<Map<String, String>> response = errorClient.getResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAppointmentWithInvalidWork() {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        int invalidWorkId = works.last().getId() + 1;
        ResponseEntity<Map<String, String>> response = errorClient.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL + "/works=" + invalidWorkId + "&blocks=1", auth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithInvalidBlock() {
        String auth = loginAsClient();
        TreeSet<Work> works = (TreeSet<Work>) integrationDataFactory.getWorks(auth);
        int workId = works.first().getId();
        int invalidBlockId = -1;
        ResponseEntity<Map<String, String>> response = errorClient.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL + "/works=" + workId + "&blocks=" + invalidBlockId, auth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithoutEnoughBlocks() {
        String auth = loginAsClient();
        Set<Work> works = integrationDataFactory.getWorks(auth);
        String allWorks = works.stream().map(work -> work.getId().toString()).collect(Collectors.joining(";works=", "works=", ""));
        TreeSet<BlockDTO> blocks = (TreeSet<BlockDTO>) integrationDataFactory.getBlocks(auth, works);
        BlockDTO invalidBlock = blocks.first();
        ResponseEntity<Map<String, String>> response = errorClient.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL + allWorks + "&blocks=" + invalidBlock.getId(), auth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
}
