package com.spanishcoders.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by agustin on 8/08/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
public class IntegrationTests {

    public static final String LOGIN_URL = "/login";
    public static final String AUTH_HEADER = "X-AUTH-TOKEN";
    public static final String CLIENT_LOGIN = "{\"username\":\"client\",\"password\":\"client\"}";
    public static final String ADMIN_LOGIN = "{\"username\":\"admin\",\"password\":\"admin\"}";

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Test
    public void contextLoads() {
    }

    public String loginAsClient() {
        return login(CLIENT_LOGIN);
    }

    public String loginAsAdmin() {
        return login(ADMIN_LOGIN);
    }

    private String login(String login) {
        ResponseEntity<String> response = testRestTemplate.postForEntity(LOGIN_URL, login, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        HttpHeaders headers = response.getHeaders();
        assertThat(headers, notNullValue());
        assertThat(headers.containsKey(AUTH_HEADER), is(true));
        String authToken = headers.get(AUTH_HEADER).get(0);
        assertThat(authToken, notNullValue());
        return authToken;
    }
}
