package com.spanishcoders.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NavigableSet;
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
    @OneToMany(mappedBy = "appointment", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
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

    public Appointment(User user, Set<Work> works, Set<Block> blocks) {
        this();
        checkAuthentication(user);
        checkWorks(works);
        checkBlocks(blocks);
        checkAuthorization(user, works);
        checkWorkLength(works, blocks);
        this.user = user;
        user.addAppointment(this);
        this.works = works;
        this.blocks.addAll(blocks);
        this.blocks.stream().forEach(block -> block.setAppointment(this));
        Block firstBlock = ((SortedSet<Block>) this.blocks).first();
        LocalTime appointmentTime = firstBlock.getStart();
        LocalDate appointmentDate = firstBlock.getWorkingDay().getDate();
        this.date = LocalDateTime.of(appointmentDate, appointmentTime);
        this.duration = Duration.of(works.stream().mapToInt(work -> work.getDuration()).sum(), ChronoUnit.MINUTES);
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

    private void checkWorkLength(Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        int worksLength = requestedWorks.stream().mapToInt(work -> work.getDuration()).sum();
        long blocksLength = requestedBlocks.stream().mapToLong(block -> block.getLength().toMinutes()).sum();
        if (blocksLength < worksLength) {
            throw new IllegalArgumentException("Can't create an Appointment without enough Blocks for the Works");
        }
    }

    private void checkAuthorization(User user, Set<Work> requestedWorks) {
        if (requestedWorks.stream().anyMatch(work -> work.getKind() == WorkKind.PRIVATE)) {
            if (Role.getRole(user) != Role.WORKER) {
                throw new AccessDeniedException("A Client can't create an Appointment with private Works");
            }
        }
    }

    private void checkBlocks(Set<Block> requestedBlocks) {
        if (CollectionUtils.isEmpty(requestedBlocks)) {
            throw new IllegalArgumentException("Can't create an Appointment without Blocks");
        }
        if (requestedBlocks.stream().anyMatch(block -> block.getAppointment() != null)) {
            throw new IllegalStateException("Can't create an Appointment with Blocks with Appointments");
        }
        if (!blocksAreContiguous(requestedBlocks)) {
            throw new IllegalArgumentException("Can't create an Appointmnet with separated Blocks");
        }
    }

    private boolean blocksAreContiguous(Set<Block> requestedBlocks) {
        boolean contiguous = true;
        Iterator<Block> blockIt = requestedBlocks.iterator();
        while (contiguous && blockIt.hasNext()) {
            Block block = blockIt.next();
            if (blockIt.hasNext()) {
                Block nextBlock = blockIt.next();
                if (!block.isContiguousTo(nextBlock)) {
                    contiguous = false;
                } else {
                    NavigableSet<Block> remainingBlocks = Sets.newTreeSet(requestedBlocks).tailSet(block, false);
                    contiguous = blocksAreContiguous(remainingBlocks);
                }
            }
        }
        return contiguous;
    }

    private void checkWorks(Set<Work> requestedWorks) {
        if (CollectionUtils.isEmpty(requestedWorks)) {
            throw new IllegalArgumentException("Can't create an Appointment without Works");
        }
    }

    private void checkAuthentication(User user) {
        if (user == null) {
            throw new AccessDeniedException("Can't create an Appointment without User");
        }
    }
}
