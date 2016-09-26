package com.spanishcoders.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spanishcoders.model.AppUser;
import com.spanishcoders.model.dto.SignInUserDTO;
import com.spanishcoders.model.security.UserAuthentication;
import com.spanishcoders.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by agustin on 31/05/16.
 */
public class TokenAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    private final TokenHandler tokenHandler;

    private final UserRepository userRepository;

    public TokenAuthenticationService(TokenHandler tokenHandler, UserRepository userRepository) {
        this.tokenHandler = tokenHandler;
        this.userRepository = userRepository;
    }

    public void addAuthentication(HttpServletResponse response, UserAuthentication authentication) throws IOException {
        //TODO: WTF ARE WE DOING MODIFYING RESPONSE IN A SERVICE??
        final UserDetails user = authentication.getDetails();
        response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
        AppUser applicationUser = userRepository.findByUsername(authentication.getName());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        new ObjectMapper().writeValue(response.getWriter(), new SignInUserDTO(applicationUser));
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

