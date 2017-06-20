package com.spanishcoders.user.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spanishcoders.user.UserDTO;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger logger = LoggerFactory.getLogger(StatelessLoginFilter.class);

	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;

	public StatelessLoginFilter(String urlMapping, TokenAuthenticationService tokenAuthenticationService,
			UserDetailsService userDetailsService, AuthenticationManager authManager, ObjectMapper objectMapper) {
		super(new AntPathRequestMatcher(urlMapping));
		this.userDetailsService = userDetailsService;
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.objectMapper = objectMapper;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		UsernamePasswordAuthenticationToken loginToken = null;
		try {
			final UserDTO user = objectMapper.readValue(request.getInputStream(), UserDTO.class);
			loginToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
		} catch (final IOException ioe) {
			logger.error("Error reading user from request : " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
		}
		return getAuthenticationManager().authenticate(loginToken);
	}

	@Override
	public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {

		// Lookup the complete AppUser object from the database and create an
		// Authentication for it
		final UserDetails authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
		final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

		// Add the custom token as HTTP header to the response
		tokenAuthenticationService.addAuthentication(response, userAuthentication);

		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
	}
}
