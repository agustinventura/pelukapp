package com.spanishcoders.integration;

import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.ClientDTO;
import com.spanishcoders.model.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by agustin on 6/08/16.
 */
public class UserTests extends IntegrationTests {

    public final static String NEXT_APPOINTMENTS_URL = "/user/appointments/next";

    @Before
    public void setUp() {
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void clientLogin() {
        loginAsClient();
    }

    @Test
    public void loginWithWrongPassword() {
        ResponseEntity<UserDTO> response = login(CLIENT_USERNAME, CLIENT_PASSWORD + "s");
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void loginWithWrongUsername() {
        ResponseEntity<UserDTO> response = login(CLIENT_USERNAME + "s", CLIENT_PASSWORD);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void adminLogin() {
        loginAsAdmin();
    }

    @Test
    public void registerUser() {

        String username = "usuario1234";
        String password = "usuario1234";
        String name = "usuario";
        String phone = "666666666";

        ResponseEntity<ClientDTO> registrationResponse = register(username, password, phone, name);
        assertThat(registrationResponse.getStatusCode(), is(HttpStatus.OK));

        ResponseEntity<UserDTO> loginResponse = login(username, password);
        assertThat(loginResponse.getStatusCode(), is(HttpStatus.OK));
    }

    private ResponseEntity<ClientDTO> register(String username, String password, String phone, String name) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setUsername(username);
        clientDTO.setPassword(password);
        clientDTO.setName(name);
        clientDTO.setPhone(phone);
        HttpEntity<ClientDTO> request = new HttpEntity<>(clientDTO);
        ResponseEntity<ClientDTO> response = testRestTemplate.postForEntity(REGISTER_CLIENT_URL, request, ClientDTO.class);
        return response;
    }

    @Test
    public void getNextAppointments() {
        String auth = loginAsClient();
        AppointmentDTO appointment = integrationDataFactory.getAppointment(auth);
        HeadersTestRestTemplate<Set<AppointmentDTO>> appointmentClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<Set<AppointmentDTO>> appointmentTypeRef = new ParameterizedTypeReference<Set<AppointmentDTO>>() {
        };
        Set<AppointmentDTO> nextAppointments = appointmentClient.getWithAuthorizationHeader(NEXT_APPOINTMENTS_URL, auth, appointmentTypeRef);
        assertThat(nextAppointments, not(empty()));
        assertThat(nextAppointments, hasItem(appointment));
    }
}
