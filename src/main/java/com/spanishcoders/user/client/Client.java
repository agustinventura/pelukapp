package com.spanishcoders.user.client;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;

@Entity
@DiscriminatorValue(value = Role.Values.CLIENT)
public class Client extends AppUser {

	private String distance;

	public Client() {
		this.setRole(Role.CLIENT);
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Client [distance=" + distance + ", getUsername()=" + getUsername() + ", getStatus()=" + getStatus()
				+ ", getName()=" + getName() + ", getPassword()=" + getPassword() + ", getId()=" + getId()
				+ ", getPhone()=" + getPhone() + "]";
	}
}
