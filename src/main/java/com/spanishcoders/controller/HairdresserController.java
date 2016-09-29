package com.spanishcoders.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.*;
import com.spanishcoders.services.HairdresserService;
import com.spanishcoders.services.WorkService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    private WorkService workService;

    public HairdresserController(HairdresserService hairdresserService, WorkService workService) {
        this.hairdresserService = hairdresserService;
        this.workService = workService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "blocks/available/{day}/{works}", method = RequestMethod.GET)
    public List<HairdresserBlocks> getFreeBlocksByDay(Authentication authentication, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
                                                      @MatrixVariable Set<Integer> works) {
        Set<Work> requestedWorks = workService.get(works);
        Map<Hairdresser, Set<Block>> freeBlocks = hairdresserService.getAvailableBlocksForDayByHairdresser(requestedWorks, day);
        return toDTOs(freeBlocks);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "schedule/today", method = RequestMethod.GET)
    public List<HairdresserSchedule> getTodaySchedule(Authentication authentication) {
        Map<Hairdresser, Set<Block>> todaysBlocks = hairdresserService.getDayBlocks(LocalDate.now());
        return toScheduleDTOs(todaysBlocks);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "schedule/{day}", method = RequestMethod.GET)
    public List<HairdresserSchedule> getDaySchedule(Authentication authentication,
                                                    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        Map<Hairdresser, Set<Block>> todaysBlocks = hairdresserService.getDayBlocks(day);
        return toScheduleDTOs(todaysBlocks);
    }

    private List<HairdresserSchedule> toScheduleDTOs(Map<Hairdresser, Set<Block>> todaysBlocks) {
        List<HairdresserSchedule> schedule = Lists.newArrayList();
        for (Map.Entry<Hairdresser, Set<Block>> entry : todaysBlocks.entrySet()) {
            HairdresserDTO hairdresser = new HairdresserDTO(entry.getKey());
            Set<ScheduleDTO> scheduleDTO = getScheduleDTOs(entry.getValue());
            schedule.add(new HairdresserSchedule(hairdresser, scheduleDTO));
        }
        return schedule;
    }

    private Set<ScheduleDTO> getScheduleDTOs(Set<Block> blocks) {
        Set<ScheduleDTO> scheduleDTOs = Sets.newTreeSet();
        blocks.stream().forEach(block -> scheduleDTOs.add(new ScheduleDTO(block)));
        return scheduleDTOs;
    }

    private List<HairdresserBlocks> toDTOs(Map<Hairdresser, Set<Block>> freeBlocks) {
        List<HairdresserBlocks> availableBlocks = Lists.newArrayList();
        for (Map.Entry<Hairdresser, Set<Block>> entry : freeBlocks.entrySet()) {
            HairdresserDTO hairdresser = new HairdresserDTO(entry.getKey());
            Set<BlockDTO> blocks = getBlockDTOs(entry.getValue());
            HairdresserBlocks hairdresserBlocks = new HairdresserBlocks(hairdresser, blocks);
            availableBlocks.add(hairdresserBlocks);
        }
        return availableBlocks;
    }

    private Set<BlockDTO> getBlockDTOs(Set<Block> blocks) {
        Set<BlockDTO> blockDTOs = Sets.newTreeSet();
        blocks.stream().forEach(block -> blockDTOs.add(new BlockDTO(block)));
        return blockDTOs;
    }
}
