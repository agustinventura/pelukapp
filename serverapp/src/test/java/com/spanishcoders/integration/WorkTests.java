package com.spanishcoders.integration;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
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

    @Before
    public void setUp() {
        this.client = new HeadersTestRestTemplate<>(testRestTemplate);
    }

    @Test
    public void getWorksWithoutAuthorization() {
        HeadersTestRestTemplate<String> errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {
        };
        String result = errorClient.getWithAuthorizationHeader(WORKS_URL, "", typeRef);
        assertThat(result, containsString("403"));
    }

    @Test
    public void getWorksAsClient() {
        String authHeader = loginAsClient();
        ParameterizedTypeReference<Set<Work>> typeRef = new ParameterizedTypeReference<Set<Work>>() {
        };
        Set<Work> works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, typeRef);
        assertThat(works, not(empty()));
        for (Work work : works) {
            assertThat(work.getKind(), is(WorkKind.PUBLIC));
        }
    }

    @Test
    public void getWorksAsAdmin() {
        String authHeader = loginAsAdmin();
        ParameterizedTypeReference<Set<Work>> typeRef = new ParameterizedTypeReference<Set<Work>>() {
        };
        Set<Work> works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, typeRef);
        assertThat(works, not(empty()));
    }

}
