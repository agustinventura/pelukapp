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

    public static final String FREE_BLOCKS_URL = "/hairdresser/blocks/free/works=1";
    private HeadersTestRestTemplate<List<HairdresserAvailableBlocks>> client;
    private ParameterizedTypeReference<List<HairdresserAvailableBlocks>> typeRef = new ParameterizedTypeReference<List<HairdresserAvailableBlocks>>() {
    };

    @Before
    public void setUp() {
        this.client = new HeadersTestRestTemplate<>(this.testRestTemplate);
    }

    @Test
    public void getAvailableBlocksWithoutAuthorization() {
        HeadersTestRestTemplate<Object> errorClient = new HeadersTestRestTemplate<>(this.testRestTemplate);
        ParameterizedTypeReference<Object> errorTypeRef = new ParameterizedTypeReference<Object>() {
        };
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAvailableBlocksAsClient() {
        String authHeader = loginAsClient();
        List<HairdresserAvailableBlocks> availableBlocks = client.getWithAuthorizationHeader(FREE_BLOCKS_URL, authHeader, typeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserAvailableBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getAvailableBlocks();
        assertThat(freeBlocks.size(), is(10));
    }
}
