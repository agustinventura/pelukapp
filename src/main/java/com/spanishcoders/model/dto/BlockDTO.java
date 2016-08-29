package com.spanishcoders.model.dto;

import com.spanishcoders.model.Block;

/**
 * Created by agustin on 8/08/16.
 */
public class BlockDTO implements Comparable<BlockDTO> {

    private Integer id;

    private String start;

    private String length;

    private Integer workingDay;

    private Integer appointment;

    public BlockDTO() {

    }

    public BlockDTO(Block block) {
        this.id = block.getId();
        this.start = block.getStart().toString();
        this.length = block.getLength().toString();
        this.workingDay = block.getWorkingDay().getId();
        this.appointment = block.getAppointment() != null ? block.getAppointment().getId() : null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockDTO blockDTO = (BlockDTO) o;

        return id != null ? id.equals(blockDTO.id) : blockDTO.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BlockDTO{" +
                "id=" + id +
                ", start='" + start + '\'' +
                ", length='" + length + '\'' +
                ", workingDay=" + workingDay +
                ", appointment=" + appointment +
                '}';
    }
}
