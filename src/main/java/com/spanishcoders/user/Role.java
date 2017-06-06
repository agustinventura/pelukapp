package com.spanishcoders.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;

public enum Role {
	WORKER(Values.WORKER), CLIENT(Values.CLIENT);

	private String name;
	private SimpleGrantedAuthority grantedAuthority;

	Role(String name) {
		this.name = name;
		this.grantedAuthority = new SimpleGrantedAuthority("ROLE_" + name);
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

	public static class Values {
		public static final String WORKER = "WORKER";
		public static final String CLIENT = "CLIENT";
	}
}
