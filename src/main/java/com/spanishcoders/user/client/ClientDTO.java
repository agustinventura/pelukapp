package com.spanishcoders.user.client;

import com.spanishcoders.user.UserDTO;

public class ClientDTO extends UserDTO {

	private String distance;

	public ClientDTO() {
		super();
	}

	public ClientDTO(Client client) {
		super(client);
		this.distance = client.getDistance();
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "ClientDTO [distance=" + distance + ", getId()=" + getId() + ", getName()=" + getName()
				+ ", getUsername()=" + getUsername() + ", getPassword()=" + getPassword() + ", getPhone()=" + getPhone()
				+ ", getStatus()=" + getStatus() + "]";
	}

}
