package com.spanishcoders.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.dto.HairdresserDTO;
import com.spanishcoders.model.dto.HairdresserSchedule;
import com.spanishcoders.model.dto.ScheduleDTO;
import com.spanishcoders.services.HairdresserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by agustin on 21/06/16.
 */
@RestController
@RequestMapping(value = "/hairdresser", produces = MediaType.APPLICATION_JSON_VALUE)
public class HairdresserController {

    private HairdresserService hairdresserService;

    public HairdresserController(HairdresserService hairdresserService) {
        this.hairdresserService = hairdresserService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "schedule/today", method = RequestMethod.GET)
    public List<HairdresserSchedule> getTodaySchedule(Authentication authentication) {
        return getDaySchedule(authentication, LocalDate.now());
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "schedule/{day}", method = RequestMethod.GET)
    public List<HairdresserSchedule> getDaySchedule(Authentication authentication,
                                                    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        Map<Hairdresser, Set<Block>> todaysBlocks = hairdresserService.getDayBlocks(day);
        return toScheduleDTOs(authentication, todaysBlocks);
    }

    private List<HairdresserSchedule> toScheduleDTOs(Authentication authentication, Map<Hairdresser, Set<Block>> todaysBlocks) {
        List<HairdresserSchedule> schedule = Lists.newArrayList();
        for (Map.Entry<Hairdresser, Set<Block>> entry : todaysBlocks.entrySet()) {
            HairdresserDTO hairdresser = new HairdresserDTO(entry.getKey());
            Set<ScheduleDTO> scheduleDTO = getScheduleDTOs(authentication, entry.getValue());
            schedule.add(new HairdresserSchedule(hairdresser, scheduleDTO));
        }
        return schedule;
    }

    private Set<ScheduleDTO> getScheduleDTOs(Authentication authentication, Set<Block> blocks) {
        Set<ScheduleDTO> scheduleDTOs = Sets.newTreeSet();
        blocks.stream().forEach(block -> scheduleDTOs.add(new ScheduleDTO(authentication, block)));
        return scheduleDTOs;
    }
}
