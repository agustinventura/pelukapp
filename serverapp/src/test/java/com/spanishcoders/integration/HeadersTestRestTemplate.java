package com.spanishcoders.integration;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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

    public ResponseEntity<T> getResponseEntityWithAuthorizationHeader(String url, String authHeader, ParameterizedTypeReference<T> typeReference) {
        HttpEntity<?> request = getHeaders(authHeader);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, request, typeReference);
        return response;
    }

    private HttpEntity<?> getHeaders(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, authHeader);
        return new HttpEntity<>(headers);
    }

    public ResponseEntity<T> postResponseEntityWithAuthorizationHeader(String url, String authHeader, ParameterizedTypeReference<T> typeReference) {
        HttpEntity<?> request = getHeaders(authHeader);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, request, typeReference);
        return response;
    }

    public T getWithAuthorizationHeader(String url, String authHeader, ParameterizedTypeReference<T> typeReference) {
        return getResponseEntityWithAuthorizationHeader(url, authHeader, typeReference).getBody();
    }

    public T postWithAuthorizationHeader(String url, String authHeader, ParameterizedTypeReference<T> typeReference) {
        return postResponseEntityWithAuthorizationHeader(url, authHeader, typeReference).getBody();
    }
}
