package com.spanishcoders.user.security;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.google.common.base.Preconditions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public final class TokenHandler {

	private static final Logger logger = LoggerFactory.getLogger(TokenHandler.class);

	private final String secret;

	private final UserDetailsService userDetailsService;

	@Value("${token_duration:1}")
	private int tokenDuration;

	public TokenHandler(String secret, UserDetailsService userService) {
		this.secret = Base64.getEncoder().encodeToString(secret.getBytes());
		this.userDetailsService = Preconditions.checkNotNull(userService);
	}

	public UserDetails parseUserFromToken(String token) {
		UserDetails userDetails = null;
		try {
			final String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
			userDetails = userDetailsService.loadUserByUsername(username);
		} catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException jwte) {
			logger.error("Used invalid JWT " + token + ": " + jwte.getMessage());
		}
		return userDetails;
	}

	public String createTokenForUser(UserDetails user) {
		final Date now = new Date();
		final Date expiration = new Date(now.getTime() + TimeUnit.HOURS.toMillis(tokenDuration));
		return Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(user.getUsername()).setIssuedAt(now)
				.setExpiration(expiration).signWith(SignatureAlgorithm.HS512, secret).compact();
	}
}
