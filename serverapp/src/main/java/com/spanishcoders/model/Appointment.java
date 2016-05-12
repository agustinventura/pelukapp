package com.spanishcoders.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */

@Data
@Entity
public class Appointment {

    @Id
    private int id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @NotNull
    @ManyToOne
    private Hairdresser hairdresser;

    @ManyToOne
    private Client client;

    /**
     * in minutes
     */
    private int duration;

    private String notes;

    @ManyToMany
    private Set<Service> bookedServices;
}
