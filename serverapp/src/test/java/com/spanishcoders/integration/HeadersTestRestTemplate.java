package com.spanishcoders.integration;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static com.spanishcoders.integration.IntegrationTests.AUTH_HEADER;

/**
 * Created by agustin on 8/08/16.
 */
public class HeadersTestRestTemplate<T> {

    private final TestRestTemplate restTemplate;

    public HeadersTestRestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public T[] getWithAuthorizationHeader(String url, String authHeader, Class<T[]> returnClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, authHeader);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<T[]> response = restTemplate.exchange(url, HttpMethod.GET, request, returnClass);
        return response.getBody();
    }
}
