package com.spanishcoders.integration;

import com.spanishcoders.model.dto.HairdresserDTO;
import com.spanishcoders.model.dto.HairdresserSchedule;
import com.spanishcoders.model.dto.ScheduleDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by agustin on 8/08/16.
 */
public class HairdresserTests extends IntegrationTests {

    public static final String TODAY_SCHEDULE_URL = "/hairdresser/schedule/today";
    public static final String DAY_SCHEDULE_URL = "/hairdresser/schedule/";
    ParameterizedTypeReference<Object> errorTypeRef = new ParameterizedTypeReference<Object>() {
    };
    private HeadersTestRestTemplate<List<HairdresserSchedule>> hairdresserScheduleClient;
    private ParameterizedTypeReference<List<HairdresserSchedule>> hairdresserScheduleTypeRef = new ParameterizedTypeReference<List<HairdresserSchedule>>() {
    };
    private HeadersTestRestTemplate<Object> errorClient;

    @Before
    public void setUp() {
        hairdresserScheduleClient = new HeadersTestRestTemplate<>(testRestTemplate);
        errorClient = new HeadersTestRestTemplate<>(testRestTemplate);
        integrationDataFactory = new IntegrationDataFactory(testRestTemplate);
    }

    @Test
    public void getScheduleWithoutAuthorization() {
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getScheduleInvalidDay() {
        String clientAuth = loginAsClient();
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(DAY_SCHEDULE_URL + "/2016-xx-01", clientAuth, errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


    @Test
    public void getScheduleNonWorkingDay() {
        String authHeader = loginAsClient();
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(DAY_SCHEDULE_URL + LocalDate.of(2016, 01, 01).format(DateTimeFormatter.ISO_LOCAL_DATE) + "/", authHeader, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
        HairdresserSchedule hairdresserSchedule = schedule.get(0);
        HairdresserDTO hairdresser = hairdresserSchedule.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<ScheduleDTO> scheduleBlocks = hairdresserSchedule.getSchedule();
        assertThat(scheduleBlocks.size(), is(0));
    }

    @Test
    public void getScheduleAsClient() {
        String authHeader = loginAsClient();
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(DAY_SCHEDULE_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/", authHeader, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
        HairdresserSchedule hairdresserSchedule = schedule.get(0);
        HairdresserDTO hairdresser = hairdresserSchedule.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<ScheduleDTO> scheduleBlocks = hairdresserSchedule.getSchedule();
        assertThat(scheduleBlocks.size(), is(notNullValue()));
    }

    @Test
    public void getScheduleAsHairdresser() {
        String authHeader = loginAsAdmin();
        List<HairdresserSchedule> hairdresserSchedule = hairdresserScheduleClient.getWithAuthorizationHeader(DAY_SCHEDULE_URL + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/", authHeader, hairdresserScheduleTypeRef);
        assertThat(hairdresserSchedule, is(not(empty())));
        HairdresserSchedule hairdresserAvailableBlocks = hairdresserSchedule.get(0);
        HairdresserDTO hairdresser = hairdresserAvailableBlocks.getHairdresser();
        assertThat(hairdresser, notNullValue());
        Set<ScheduleDTO> scheduleBlocks = hairdresserAvailableBlocks.getSchedule();
        assertThat(scheduleBlocks.size(), is(notNullValue()));
    }

    @Test
    public void getTodayScheduleWithoutAuthorization() {
        ResponseEntity<Object> response = errorClient.getResponseEntityWithAuthorizationHeader(TODAY_SCHEDULE_URL, "", errorTypeRef);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void getTodayScheduleAsClient() {
        String clientAuth = loginAsClient();
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(TODAY_SCHEDULE_URL, clientAuth, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
        assertThat(schedule.stream().filter(hairdresserSchedule -> getScheduleWithOtherClients(hairdresserSchedule)).collect(Collectors.toSet()), is(empty()));
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
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(DAY_SCHEDULE_URL +
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), clientAuth, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
        assertThat(schedule.stream().filter(hairdresserSchedule -> getScheduleWithOtherClients(hairdresserSchedule)).collect(Collectors.toSet()), is(empty()));
    }

    private boolean getScheduleWithOtherClients(HairdresserSchedule hairdresserSchedule) {
        return hairdresserSchedule.getSchedule().stream().anyMatch(scheduleDTO -> (!StringUtils.isEmpty(scheduleDTO.getClient()) && !scheduleDTO.getClient().equals(CLIENT_USERNAME)));
    }

    @Test
    public void getDayScheduleAsAdmin() {
        String adminAuth = loginAsAdmin();
        List<HairdresserSchedule> schedule = hairdresserScheduleClient.getWithAuthorizationHeader(DAY_SCHEDULE_URL +
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), adminAuth, hairdresserScheduleTypeRef);
        assertThat(schedule, is(not(empty())));
    }
}
