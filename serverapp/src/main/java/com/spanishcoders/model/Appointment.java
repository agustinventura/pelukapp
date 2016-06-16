package com.spanishcoders.model;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */

@Entity
public class Appointment {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(mappedBy = "appointment")
    private Set<Block> blocks;

    @ManyToMany
    private Set<Work> works;

    @ManyToOne
    private Client client;

    private LocalDateTime date;

    private Duration duration;
}
