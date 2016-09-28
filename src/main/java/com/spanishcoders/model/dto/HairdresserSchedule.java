package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by agustin on 28/09/16.
 */
public class HairdresserSchedule {

    private final HairdresserDTO hairdresser;

    private final Set<ScheduleDTO> schedule;

    public HairdresserSchedule() {
        hairdresser = new HairdresserDTO();
        schedule = Sets.newHashSet();
    }

    public HairdresserSchedule(HairdresserDTO hairdresser, Set<ScheduleDTO> schedule) {
        this.hairdresser = hairdresser;
        this.schedule = schedule;
    }

    public HairdresserDTO getHairdresser() {
        return hairdresser;
    }

    public Set<ScheduleDTO> getSchedule() {
        return schedule;
    }
}
