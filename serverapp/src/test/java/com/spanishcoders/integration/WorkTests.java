package com.spanishcoders.integration;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
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
        this.client = new HeadersTestRestTemplate<>(testRestTemplate);
    }

    @Test
    public void getWorksWithoutAuthorization() {
        HeadersTestRestTemplate<Object> errorClient = new HeadersTestRestTemplate<>(this.testRestTemplate);
        ParameterizedTypeReference<Object> errorTypeRef = new ParameterizedTypeReference<Object>() {
        };
        ResponseEntity<Object> result = errorClient.getResponseEntityWithAuthorizationHeader(WORKS_URL, "", errorTypeRef);
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
