package com.spanishcoders.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;

/**
 * Created by agustin on 15/06/16.
 */
public enum Role {
    WORKER("ROLE_WORKER"), CLIENT("ROLE_CLIENT");

    private String name;
    private SimpleGrantedAuthority grantedAuthority;

    Role(String name) {
        this.name = name;
        this.grantedAuthority = new SimpleGrantedAuthority(name);
    }

    public static Role getRole(AppUser user) {
        if (user instanceof Client) {
            return Role.CLIENT;
        } else if (user instanceof Hairdresser) {
            return Role.WORKER;
        } else {
            throw new IllegalArgumentException("unexpected user type");
        }
    }

    public String getName() {
        return this.name;
    }

    public SimpleGrantedAuthority getGrantedAuthority() {
        return this.grantedAuthority;
    }
}
