package com.spanishcoders.model;

import javax.persistence.Entity;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Client extends User {

    private String distance;

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
