package com.spanishcoders.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by pep on 12/05/2016.
 */

@Data
@Entity
public class Service {

    @Id
    private int id;

    private String name;

    /**
     * in minutes
     */
    private int duration;
}
