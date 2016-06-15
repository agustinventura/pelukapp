package com.spanishcoders.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public String getName() {
        return this.name;
    }

    public SimpleGrantedAuthority getGrantedAuthority() {
        return this.grantedAuthority;
    }
}
