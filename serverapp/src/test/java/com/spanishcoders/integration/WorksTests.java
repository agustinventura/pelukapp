package com.spanishcoders.integration;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.core.Is.is;

/**
 * Created by agustin on 8/08/16.
 */
public class WorksTests extends IntegrationTests {

    public static final String WORKS_URL = "/works";

    private HeadersTestRestTemplate<Work> client;

    @Before
    public void setUp() {
        this.client = new HeadersTestRestTemplate<Work>(testRestTemplate);
    }

    @Test
    public void getWorksAsClient() {
        String authHeader = loginAsClient();
        Work[] works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, Work[].class);
        assertThat(works, not(emptyArray()));
        for (Work work : works) {
            assertThat(work.getKind(), is(WorkKind.PUBLIC));
        }
    }

    @Test
    public void getWorksAsAdmin() {
        String authHeader = loginAsAdmin();
        Work[] works = client.getWithAuthorizationHeader(WORKS_URL, authHeader, Work[].class);
        assertThat(works, not(emptyArray()));
    }

}
