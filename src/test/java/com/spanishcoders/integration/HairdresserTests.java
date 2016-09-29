package com.spanishcoders.integration;

import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserBlocks;
import com.spanishcoders.model.dto.HairdresserDTO;
import com.spanishcoders.model.dto.HairdresserSchedule;
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
    public static final String TODAY_SCHEDULE_URL = "/hairdresser/schedule/today";
    public static final String DAY_SCHEDULE_URL = "/hairdresser/schedule/";
    ParameterizedTypeReference<Object> errorTypeRef = new ParameterizedTypeReference<Object>() {
    };
    private HeadersTestRestTemplate<List<HairdresserBlocks>> hairdresserBlocksClient;
    private ParameterizedTypeReference<List<HairdresserBlocks>> hairdresserBlocksTypeRef = new ParameterizedTypeReference<List<HairdresserBlocks>>() {
    };
    private HeadersTestRestTemplate<List<HairdresserSchedule>> hairdresserScheduleClient;
    private ParameterizedTypeReference<List<HairdresserSchedule>> hairdresserScheduleTypeRef = new ParameterizedTypeReference<List<HairdresserSchedule>>() {
    };
    private HeadersTestRestTemplate<Object> errorClient;

    @Before
    public void setUp() {
        hairdresserBlocksClient = new HeadersTestRestTemplate<>(testRestTemplate);
        hairdresserScheduleClient = new HeadersTestRestTemplate<>(testRestTemplate);
        errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getAvailableBlocksWithoutAuthorization() {
        String worksUrl = integrationDataFactory.getWorksUrl(loginAsClient());
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL + worksUrl, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getAvailableBlocksInvalidDay() {
        String clientAuth = loginAsClient();
        String worksUrl = integrationDataFactory.getWorksUrl(clientAuth);
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL + "/2016-xx-01" + worksUrl, clientAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getAvailableBlocksWithoutWorks() {
        String clientAuth = loginAsClient();
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL +
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/works", clientAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAvailableBlocksInvalidWorks() {
        String clientAuth = loginAsClient();
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(FREE_BLOCKS_URL +
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/works=a", clientAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAvailableBlocksNonWorkingDay() {
        String authHeader = loginAsClient();
        String worksUrl = integrationDataFactory.getWorksUrl(authHeader);
        List<HairdresserBlocks> availableBlocks = hairdresserBlocksClient.getWithAuthorizationHeader(FREE_BLOCKS_URL + LocalDate.of(2016, 01, 01).format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + worksUrl, authHeader, hairdresserBlocksTypeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getBlocks();
        assertThat(freeBlocks.size(), is(0));
    }

    @Test
    public void getAvailableBlocksAsClient() {
        String authHeader = loginAsClient();
        String worksUrl = integrationDataFactory.getWorksUrl(authHeader);
        List<HairdresserBlocks> availableBlocks = hairdresserBlocksClient.getWithAuthorizationHeader(FREE_BLOCKS_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + worksUrl, authHeader, hairdresserBlocksTypeRef);
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
        List<HairdresserBlocks> availableBlocks = hairdresserBlocksClient.getWithAuthorizationHeader(FREE_BLOCKS_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + worksUrl, authHeader, hairdresserBlocksTypeRef);
        assertThat(availableBlocks, is(not(empty())));
        HairdresserBlocks hairdresserAvailableBlocks = availableBlocks.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<BlockDTO> freeBlocks = hairdresserAvailableBlocks.getBlocks();
        assertThat(freeBlocks.size(), is(notNullValue()));
    }

    @Test
    public void getTodayScheduleWithoutAuthorization() {
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(TODAY_SCHEDULE_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getTodayScheduleAsClient() {
        String clientAuth = loginAsClient();
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(TODAY_SCHEDULE_URL, clientAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getTodayScheduleAsAdmin() {
        String adminAuth = loginAsAdmin();
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(TODAY_SCHEDULE_URL, adminAuth, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
    }

    @Test
    public void getDayScheduleWithoutAuthorization() {
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getDayScheduleInvalidDay() {
        String adminAuth = loginAsAdmin();
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL +
                "2016-xy-01", adminAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getDayScheduleAsClient() {
        String clientAuth = loginAsClient();
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL +
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), clientAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getDayScheduleAsAdmin() {
        String adminAuth = loginAsAdmin();
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(DAY_SCHEDULE_URL +
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), adminAuth, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
    }
}
