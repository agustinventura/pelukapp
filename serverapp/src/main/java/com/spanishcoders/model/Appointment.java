package com.spanishcoders.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotEmpty
    @OneToMany(mappedBy = "appointment")
    private Set<Block> blocks;

    @NotEmpty
    @ManyToMany
    private Set<Work> works;

    @ManyToOne
    private Client client;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Duration duration;

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

    public Set<Work> getWorks() {
        return works;
    }

    public void setWorks(Set<Work> works) {
        this.works = works;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
