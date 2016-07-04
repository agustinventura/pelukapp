package com.spanishcoders.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class WorkingDay implements Comparable<WorkingDay> {

    @Id
    @GeneratedValue
    protected Integer id;

    @NotNull
    private LocalDate date;

    @NotNull
    @ManyToOne
    private Agenda agenda;

    @NotEmpty
    @OneToMany(mappedBy = "workingDay")
    @JsonManagedReference
    private NavigableSet<Block> blocks;

    public WorkingDay() {
        this.blocks = new TreeSet<>();
    }

    public WorkingDay(Agenda agenda) {
        this();
        this.date = getNewWorkingDayDate(agenda.getNonWorkingDays(), agenda.getWorkingDays());
        NavigableSet<Block> workingDayBlocks = createBlocksForDay(agenda.getCurrentTimetable());
        this.setBlocks(workingDayBlocks);
        this.agenda = agenda;
        agenda.addWorkingDay(this);
    }

    public WorkingDay(Agenda agenda, LocalDate date) {
        this();
        if (agenda.getNonWorkingDays().contains(date)) {
            throw new IllegalArgumentException("Can't create working day on agenda " + agenda + " non working day: " + date);
        }
        this.agenda = agenda;
        this.date = date;
        agenda.addWorkingDay(this);
        this.setBlocks(createBlocksForDay(agenda.getCurrentTimetable()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public NavigableSet<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(NavigableSet<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public String toString() {
        return "WorkingDay{" +
                "id=" + id +
                ", date=" + date +
                ", agenda=" + agenda +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkingDay that = (WorkingDay) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return agenda != null ? agenda.equals(that.agenda) : that.agenda == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (agenda != null ? agenda.hashCode() : 0);
        return result;
    }

    public void addBlock(Block block) {
        if (block == null) {
            throw new IllegalArgumentException("Can't add an empty block to working day");
        }
        this.blocks.add(block);
    }

    private LocalDate getNewWorkingDayDate(Set<LocalDate> nonWorkingDays, SortedMap<LocalDate, WorkingDay> workingDays) {
        LocalDate lastWorkingDayDate = null;
        if (workingDays.isEmpty()) {
            lastWorkingDayDate = LocalDate.now();
        } else {
            lastWorkingDayDate = workingDays.lastKey().plusDays(1);
        }
        if (nonWorkingDays != null && !nonWorkingDays.isEmpty()) {
            while (nonWorkingDays.contains(lastWorkingDayDate)) {
                lastWorkingDayDate = lastWorkingDayDate.plusDays(1);
            }
        }
        return lastWorkingDayDate;
    }

    private NavigableSet<Block> createBlocksForDay(Timetable timetable) {
        NavigableSet<Block> newBlocks = new TreeSet<>();
        for (Stretch stretch : timetable.getStretches()) {
            LocalTime startTime = stretch.getStart();
            while (startTime.isBefore(stretch.getEnd())) {
                Block newBlock = new Block(startTime, this);
                newBlocks.add(newBlock);
                startTime = startTime.plus(Block.DEFAULT_BLOCK_LENGTH);
            }
        }
        return newBlocks;
    }

    public Set<Block> getAvailableBlocks(Set<Work> works) {
        Set<Block> availableBlocks = Sets.newHashSet();
        if (works != null && !works.isEmpty()) {
            for (Block block : blocks) {
                if (isAvailable(block, getWorksTotalLength(works))) {
                    availableBlocks.add(block);
                }
            }
        }
        return availableBlocks;
    }

    private boolean isAvailable(Block block, int totalLength) {
        if (block.getAppointment() != null) {
            return false;
        } else {
            totalLength -= block.getLength().toMinutes();
            if (totalLength <= 0) {
                return true;
            } else {
                NavigableSet<Block> nextBlocks = blocks.tailSet(block, false);
                if (nextBlocks.isEmpty()) {
                    return false;
                } else {
                    Block nextBlock = nextBlocks.first();
                    if (block.isContiguousTo(nextBlock)) {
                        return isAvailable(nextBlock, totalLength);
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    private int getWorksTotalLength(Set<Work> works) {
        int totalLength = 0;
        for (Work work : works) {
            totalLength += work.getDuration();
        }
        return totalLength;
    }

    @Override
    public int compareTo(WorkingDay o) {
        return date.compareTo(o.getDate());
    }
}
