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
}
