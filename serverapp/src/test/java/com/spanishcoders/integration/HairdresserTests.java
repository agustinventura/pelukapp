package com.spanishcoders.integration;

import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserAvailableBlocks;
import com.spanishcoders.model.dto.HairdresserDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by agustin on 8/08/16.
 */
public class HairdresserTests extends IntegrationTests {

    public static final String FREE_BLOCKS_URL = "/hairdresser/blocks/free/";

    private HeadersTestRestTemplate<List<HairdresserAvailableBlocks>> client;
    private ParameterizedTypeReference<List<HairdresserAvailableBlocks>> typeRef = new ParameterizedTypeReference<List<HairdresserAvailableBlocks>>() {
    };

    @Before
    public void setUp() {
        client = new HeadersTestRestTemplate<>(testRestTemplate);
        errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getAvailableBlocksWithoutAuthorization() {
        String worksUrl = integrationDataFactory.getWorksUrl(loginAsClient());
        ResponseEntity<Map<String, String>> response = errorClient.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL + worksUrl, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAvailableBlocksAsClient() {
        String authHeader = loginAsClient();
        String worksUrl = integrationDataFactory.getWorksUrl(authHeader);
        List<HairdresserAvailableBlocks> availableBlocks = client.getWithAuthorizationHeader(FREE_BLOCKS_URL + worksUrl, authHeader, typeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserAvailableBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getAvailableBlocks();
        assertThat(freeBlocks.size(), is(10));
    }

    @Test
    public void getAvailableBlocksAsHairdresser() {
        String authHeader = loginAsAdmin();
        String worksUrl = integrationDataFactory.getWorksUrl(authHeader);
        List<HairdresserAvailableBlocks> availableBlocks = client.getWithAuthorizationHeader(FREE_BLOCKS_URL + worksUrl, authHeader, typeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserAvailableBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getAvailableBlocks();
        assertThat(freeBlocks.size(), is(10));
    }
}
