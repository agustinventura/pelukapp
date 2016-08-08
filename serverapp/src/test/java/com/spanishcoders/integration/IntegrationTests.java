package com.spanishcoders.integration;

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

    @Autowired
    private TestRestTemplate testRestTemplate;

    public String loginAsClient() {
        String login = "{\"username\":\"client\",\"password\":\"client\"}";
        return login(login);
    }

    public String loginAsAdmin() {
        String login = "{\"username\":\"admin\",\"password\":\"admin\"}";
        return login(login);
    }

    private String login(String login) {
        ResponseEntity<String> response = testRestTemplate.postForEntity("/login", login, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        HttpHeaders headers = response.getHeaders();
        assertThat(headers, notNullValue());
        assertThat(headers.containsKey("X-AUTH-TOKEN"), is(true));
        String authToken = headers.get("X-AUTH-TOKEN").get(0);
        assertThat(authToken, notNullValue());
        return authToken;
    }
}
