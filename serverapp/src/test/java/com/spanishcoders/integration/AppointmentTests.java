package com.spanishcoders.integration;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
        errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
    }

    @Test
    public void getAppointmentWithoutAuthorization() {
        ResponseEntity<Map<String, String>> response = errorClient.getResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAppointmentWithInvalidWork() {
        String auth = loginAsClient();
        TreeSet<Work> works = getWorks(auth);
        int invalidWorkId = works.last().getId() + 1;
        ResponseEntity<Map<String, String>> response = errorClient.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL + "/works=" + invalidWorkId + "&blocks=1", auth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAppointmentWithInvalidBlock() {
        String auth = loginAsClient();
        TreeSet<Work> works = getWorks(auth);
        int workId = works.first().getId();
        int invalidBlockId = -1;
        ResponseEntity<Map<String, String>> response = errorClient.postResponseEntityWithAuthorizationHeader(NEW_APPOINTMENT_URL + "/works=" + workId + "&blocks=" + invalidBlockId, auth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private TreeSet<Work> getWorks(String auth) {
        HeadersTestRestTemplate<Set<Work>> worksClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<Set<Work>> worksTypeRef = new ParameterizedTypeReference<Set<Work>>() {
        };
        return Sets.newTreeSet(worksClient.getWithAuthorizationHeader(WorkTests.WORKS_URL, auth, worksTypeRef));
    }
}
