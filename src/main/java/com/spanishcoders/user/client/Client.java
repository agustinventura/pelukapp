package com.spanishcoders.user.client;

import javax.persistence.Entity;

import com.spanishcoders.user.AppUser;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Client extends AppUser {

	private String distance;

	public Client() {

	}

	public Client(ClientDTO clientDTO) {
		super(clientDTO);
		this.distance = clientDTO.getDistance();
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
