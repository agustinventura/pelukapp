package com.spanishcoders.user.security;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserRepository;
import com.spanishcoders.user.UserStatus;

@Service("pelukappUserDetailsService")
public class PelukappUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(PelukappUserDetailsService.class);

	private final UserRepository userRepository;

	@Autowired
	public PelukappUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final AppUser user = userRepository.findByUsername(username);
		if (user == null) {
			logger.error("Couldn't find logging user " + username);
			throw new UsernameNotFoundException("Username " + username + " not found");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.getStatus() == UserStatus.ACTIVE, true, true, true, getAuthorities(user));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(AppUser user) {
		final Set<GrantedAuthority> authorities = Sets.newHashSet();
		authorities.add(Role.getRole(user).getGrantedAuthority());
		return authorities;
	}
}