package com.spanishcoders.services.security;

import com.spanishcoders.model.*;
import com.spanishcoders.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */
@Service("pelukappUserDetailsService")
public class PelukappUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public PelukappUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getStatus() == UserStatus.ACTIVE, true, true, true, getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user instanceof Client) {
            authorities.add(Role.CLIENT.getGrantedAuthority());
        } else if (user instanceof Hairdresser) {
            authorities.add(Role.WORKER.getGrantedAuthority());
        } else {
            throw new IllegalArgumentException("unexpected user type");
        }

        return authorities;
    }
}