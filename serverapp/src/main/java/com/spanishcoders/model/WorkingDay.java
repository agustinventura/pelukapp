package com.spanishcoders.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private Set<Block> blocks;

    public WorkingDay() {
        this.blocks = new TreeSet<>();
    }

    public WorkingDay(Agenda agenda) {
        this();
        this.date = getNewWorkingDayDate(agenda.getNonWorkingDays(), agenda.getWorkingDays());
        Set<Block> workingDayBlocks = createBlocksForDay(agenda.getCurrentTimetable());
        this.setBlocks(workingDayBlocks);
        agenda.addWorkingDay(this);
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

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public String toString() {
        return "WorkingDay{" +
                "date=" + date +
                ", agenda=" + agenda.getId() +
                ", blocks=" + blocks +
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

    private Set<Block> createBlocksForDay(Timetable timetable) {
        Set<Block> newBlocks = new TreeSet<>();
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
        return null;
    }

    @Override
    public int compareTo(WorkingDay o) {
        return o.getDate().compareTo(date);
    }
}
