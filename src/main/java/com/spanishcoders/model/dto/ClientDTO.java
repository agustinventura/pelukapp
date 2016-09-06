package com.spanishcoders.model.dto;

import com.spanishcoders.model.Client;

/**
 * Created by agustin on 6/09/16.
 */
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
