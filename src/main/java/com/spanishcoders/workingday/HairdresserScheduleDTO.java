package com.spanishcoders.workingday;

import com.google.common.collect.Sets;
import com.spanishcoders.user.hairdresser.HairdresserDTO;

import java.util.Set;

/**
 * Created by agustin on 28/09/16.
 */
public class HairdresserScheduleDTO {

    private final HairdresserDTO hairdresser;

    private final Set<ScheduleDTO> schedule;

    public HairdresserScheduleDTO() {
        hairdresser = new HairdresserDTO();
        schedule = Sets.newHashSet();
    }

    public HairdresserScheduleDTO(HairdresserDTO hairdresser, Set<ScheduleDTO> schedule) {
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
