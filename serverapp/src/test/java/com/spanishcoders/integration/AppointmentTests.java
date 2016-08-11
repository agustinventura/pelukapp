package com.spanishcoders.integration;

import com.spanishcoders.model.dto.AppointmentDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

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
}
