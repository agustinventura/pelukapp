package com.spanishcoders.integration;

import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

/**
 * Created by agustin on 8/08/16.
 */
public class WorkTests extends IntegrationTests {

    public static final String WORKS_URL = "/works";

    private HeadersTestRestTemplate<Set<Work>> client;
    private ParameterizedTypeReference<Set<Work>> typeRef = new ParameterizedTypeReference<Set<Work>>() {
    };

    @Before
    public void setUp() {
        client = new HeadersTestRestTemplate<>(testRestTemplate);
    }

    @Test
    public void getWorksWithoutAuthorization() {
        HeadersTestRestTemplate<Work> client = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<Work> typeRef = new ParameterizedTypeReference<Work>() {
        };
        ResponseEntity<Work> result = client.getResponseEntityWithAuthorizationHeader(WORKS_URL, "", typeRef);
        assertThat(result.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getWorksAsClient() {
        String authHeader = loginAsClient();
        Set<Work> works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, typeRef);
        assertThat(works, not(empty()));
        for (Work work : works) {
            assertThat(work.getKind(), is(WorkKind.PUBLIC));
        }
    }

    @Test
    public void getWorksAsAdmin() {
        String authHeader = loginAsAdmin();
        Set<Work> works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, typeRef);
        assertThat(works, not(empty()));
    }
}
