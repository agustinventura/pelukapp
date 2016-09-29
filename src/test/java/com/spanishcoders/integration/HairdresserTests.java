package com.spanishcoders.integration;

import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserBlocks;
import com.spanishcoders.model.dto.HairdresserDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public static final String FREE_BLOCKS_URL = "/hairdresser/blocks/available/";
    public static final String TODAYS_BLOCKS_URL = "/hairdresser/schedule/today";

    private HeadersTestRestTemplate<List<HairdresserBlocks>> client;
    private ParameterizedTypeReference<List<HairdresserBlocks>> typeRef = new ParameterizedTypeReference<List<HairdresserBlocks>>() {
    };

    @Before
    public void setUp() {
        client = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getAvailableBlocksWithoutAuthorization() {
        String worksUrl = integrationDataFactory.getWorksUrl(loginAsClient());
        HeadersTestRestTemplate<HairdresserBlocks> client = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<HairdresserBlocks> typeRef = new ParameterizedTypeReference<HairdresserBlocks>() {
        };
        ResponseEntity<HairdresserBlocks> response = client.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL + worksUrl, "", typeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAvailableBlocksAsClient() {
        String authHeader = loginAsClient();
        String worksUrl = integrationDataFactory.getWorksUrl(authHeader);
        List<HairdresserBlocks> availableBlocks = client.getWithAuthorizationHeader(FREE_BLOCKS_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + worksUrl, authHeader, typeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getBlocks();
        assertThat(freeBlocks.size(), is(notNullValue()));
    }

    @Test
    public void getAvailableBlocksAsHairdresser() {
        String authHeader = loginAsAdmin();
        String worksUrl = integrationDataFactory.getWorksUrl(authHeader);
        List<HairdresserBlocks> availableBlocks = client.getWithAuthorizationHeader(FREE_BLOCKS_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + worksUrl, authHeader, typeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getBlocks();
        assertThat(freeBlocks.size(), is(notNullValue()));
    }

    @Test
    public void getTodaysBlocks() {
        String authHeader = loginAsAdmin();
        List<HairdresserBlocks> todaysBlocks = client.getWithAuthorizationHeader(TODAYS_BLOCKS_URL, authHeader, typeRef);
        List<HairdresserBlocks> todaysBlocksAgain = client.getWithAuthorizationHeader(TODAYS_BLOCKS_URL, authHeader, typeRef);
    }
}
