package com.spanishcoders.model;

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

    @NotNull
    @OneToMany(mappedBy = "appointment", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Set<Block> blocks;

    @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Work> works;

    @NotNull
    @ManyToOne
    private AppUser user;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Duration duration;

    @NotNull
    private AppointmentStatus status;

    private String notes;

    public Appointment() {
        blocks = Sets.newTreeSet();
        works = Sets.newTreeSet();
        status = AppointmentStatus.VALID;
    }

    public Appointment(AppUser user, Set<Work> works, Set<Block> blocks, String notes) {
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

        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

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

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Duration getDuration() {
        return duration;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
                ", notes=" + notes +
                '}';
    }

    private void checkWorkLength(Set<Work> requestedWorks, Set<Block> requestedBlocks) {
        int worksLength = requestedWorks.stream().mapToInt(work -> work.getDuration()).sum();
        long blocksLength = requestedBlocks.stream().mapToLong(block -> block.getLength().toMinutes()).sum();
        if (notEnoughBlocks(worksLength, blocksLength) && !tooManyblocks(worksLength, blocksLength)) {
            throw new IllegalArgumentException("Can't create an Appointment without enough Blocks for the Works");
        }
    }

    private boolean tooManyblocks(int worksLength, long blocksLength) {
        return (blocksLength - worksLength) > Block.DEFAULT_BLOCK_LENGTH.toMinutes();
    }

    private boolean notEnoughBlocks(int worksLength, long blocksLength) {
        return blocksLength < worksLength;
    }

    private void checkAuthorization(AppUser user, Set<Work> requestedWorks) {
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

    private void checkAuthentication(AppUser user) {
        if (user == null) {
            throw new AccessDeniedException("Can't create an Appointment without AppUser");
        }
    }

    public void cancel() {
        blocks.stream().forEach(block -> block.setAppointment(null));
        blocks.clear();
        status = AppointmentStatus.CANCELLED;
    }
}
