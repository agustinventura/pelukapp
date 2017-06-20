package com.spanishcoders.user.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spanishcoders.user.UserDTO;
import com.spanishcoders.user.UserServiceFacade;

public class TokenAuthenticationService {

	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;

	private final UserServiceFacade userServiceFacade;

	private final ObjectMapper objectMapper;

	public TokenAuthenticationService(TokenHandler tokenHandler, UserServiceFacade userServiceFacade,
			ObjectMapper objectMapper) {
		this.tokenHandler = tokenHandler;
		this.userServiceFacade = userServiceFacade;
		this.objectMapper = objectMapper;
	}

	public void addAuthentication(HttpServletResponse response, UserAuthentication authentication) throws IOException {
		// TODO: WTF ARE WE DOING MODIFYING RESPONSE IN A SERVICE??
		final UserDetails user = authentication.getDetails();
		response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
		final UserDTO userDTO = userServiceFacade.get(authentication.getName());
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		objectMapper.writeValue(response.getWriter(), userDTO);
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		UserAuthentication userAuthentication = null;
		if (token != null) {
			final UserDetails user = tokenHandler.parseUserFromToken(token);
			if (user != null) {
				userAuthentication = new UserAuthentication(user);
			}
		} else {
			logger.error("Tried to authenticate request without auth_header");
		}
		return userAuthentication;
	}
}
