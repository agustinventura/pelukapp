package com.spanishcoders.model.dto;

import com.spanishcoders.model.Block;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Created by agustin on 8/08/16.
 */
public class BlockDTO implements Comparable<BlockDTO> {

    private Integer id;

    private LocalTime start;

    private Duration length;

    private Integer workingDay;

    private Integer appointment;

    public BlockDTO(Block block) {
        this.id = block.getId();
        this.start = block.getStart();
        this.length = block.getLength();
        this.workingDay = block.getWorkingDay().getId();
        this.appointment = block.getAppointment() != null ? block.getAppointment().getId() : null;
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

    public Integer getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Integer workingDay) {
        this.workingDay = workingDay;
    }

    public Integer getAppointment() {
        return appointment;
    }

    public void setAppointment(Integer appointment) {
        this.appointment = appointment;
    }

    @Override
    public int compareTo(BlockDTO o) {
        if (start.equals(o.getStart())) {
            return workingDay.compareTo(o.getWorkingDay());
        } else {
            return start.compareTo(o.getStart());
        }
    }
}
