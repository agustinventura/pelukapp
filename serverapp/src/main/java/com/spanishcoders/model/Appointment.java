package com.spanishcoders.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by pep on 12/05/2016.
 */

@Entity
public class Appointment implements Comparable<Appointment> {

    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty
    @OneToMany(mappedBy = "appointment")
    @JsonManagedReference
    private Set<Block> blocks;

    @NotEmpty
    @ManyToMany
    private Set<Work> works;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Duration duration;

    public Appointment() {
        blocks = Sets.newTreeSet();
        works = Sets.newTreeSet();
    }

    public Appointment(User requestUser, Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        this();
        this.user = requestUser;
        user.addAppointment(this);
        this.works = requestedWorks;
        this.blocks.addAll(requestedBlocks);
        this.blocks.stream().forEach(block -> block.setAppointment(this));
        Block firstBlock = ((SortedSet<Block>) this.blocks).first();
        LocalTime appointmentTime = firstBlock.getStart();
        LocalDate appointmentDate = firstBlock.getWorkingDay().getDate();
        this.date = LocalDateTime.of(appointmentDate, appointmentTime);
        this.duration = Duration.of(requestedWorks.stream().mapToInt(work -> work.getDuration()).sum(), ChronoUnit.MINUTES);
    }

    public Integer getId() {
        return id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public int compareTo(Appointment o) {
        return this.date.compareTo(o.getDate());
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", duration=" + duration +
                ", date=" + date +
                ", blocks=" + blocks.stream().mapToInt(block -> block.getId()).toArray() +
                ", works=" + works.stream().mapToInt(work -> work.getId()).toArray() +
                '}';
    }
}
