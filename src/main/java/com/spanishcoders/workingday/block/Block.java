package com.spanishcoders.workingday.block;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.workingday.WorkingDay;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class Block implements Comparable<Block> {

    public final static Duration DEFAULT_BLOCK_LENGTH = Duration.of(30, ChronoUnit.MINUTES);

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private LocalTime start;

    @NotNull
    private Duration length;

    @NotNull
    @ManyToOne
    private WorkingDay workingDay;

    @ManyToOne
    private Appointment appointment;

    public Block() {
        this.length = DEFAULT_BLOCK_LENGTH;
    }

    public Block(LocalTime start, WorkingDay workingDay) {
        this();
        this.start = start;
        this.workingDay = workingDay;
        workingDay.addBlock(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public Duration getLength() {
        return length;
    }

    public void setLength(Duration length) {
        this.length = length;
    }

    public WorkingDay getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(WorkingDay workingDay) {
        this.workingDay = workingDay;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", start=" + start +
                ", length=" + length +
                ", workingDay=" + workingDay +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        if (id != null ? !id.equals(block.id) : block.id != null) return false;
        if (start != null ? !start.equals(block.start) : block.start != null) return false;
        return workingDay != null ? workingDay.equals(block.workingDay) : block.workingDay == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (workingDay != null ? workingDay.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Block o) {
        if (start.equals(o.getStart())) {
            return workingDay.compareTo(o.getWorkingDay());
        } else {
            return start.compareTo(o.getStart());
        }
    }

    public boolean isContiguousTo(Block nextBlock) {
        boolean contiguous = false;
        if (nextBlock != null && nextBlock.getStart() != null) {
            LocalTime nextBlockShouldStartAt = this.getStart().plus(this.getLength());
            contiguous = nextBlockShouldStartAt.equals(nextBlock.getStart());
        }
        return contiguous;
    }
}
