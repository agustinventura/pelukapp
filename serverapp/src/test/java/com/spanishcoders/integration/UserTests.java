package com.spanishcoders.integration;

import com.spanishcoders.model.dto.UserDTO;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by agustin on 6/08/16.
 */
public class UserTests extends IntegrationTests {

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

        ResponseEntity<UserDTO> registrationResponse = register(username, password, phone, name);
        assertThat(registrationResponse.getStatusCode(), is(HttpStatus.OK));

        ResponseEntity<UserDTO> loginResponse = login(username, password);
        assertThat(loginResponse.getStatusCode(), is(HttpStatus.OK));


    }
}
