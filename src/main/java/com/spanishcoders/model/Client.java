package com.spanishcoders.model;

import com.spanishcoders.model.dto.ClientDTO;

import javax.persistence.Entity;

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
        return "Client{" +
                "username=" + username +
                "distance='" + distance + '\'' +
                '}';
    }
}
