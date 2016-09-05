package com.spanishcoders.controller;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserAvailableBlocks;
import com.spanishcoders.model.dto.HairdresserDTO;
import com.spanishcoders.services.HairdresserService;
import com.spanishcoders.services.WorkService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @RequestMapping(value = "blocks/free/{works}", method = RequestMethod.GET)
    public List<HairdresserAvailableBlocks> getFreeBlocks(Authentication authentication, @MatrixVariable Set<Integer> works) {
        Set<Work> requestedWorks = workService.get(works);
        Map<Hairdresser, Set<Block>> freeBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(requestedWorks);
        return toDTOs(freeBlocks);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "blocks/free/{day}/{works}", method = RequestMethod.GET)
    public List<HairdresserAvailableBlocks> getFreeBlocksByDay(Authentication authentication, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
                                                               @MatrixVariable Set<Integer> works) {
        Set<Work> requestedWorks = workService.get(works);
        Map<Hairdresser, Set<Block>> freeBlocks = hairdresserService.getAvailableBlocksForDayByHairdresser(requestedWorks, day);
        return toDTOs(freeBlocks);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "schedule/today", method = RequestMethod.GET)
    public List<HairdresserAvailableBlocks> getDaySchedule(Authentication authentication) {
        Map<Hairdresser, Set<Block>> todaysBlocks = hairdresserService.getTodaysBlocksByHairdresser();
        return toDTOs(todaysBlocks);
    }

    private List<HairdresserAvailableBlocks> toDTOs(Map<Hairdresser, Set<Block>> freeBlocks) {
        List<HairdresserAvailableBlocks> availableBlocks = new ArrayList<>(freeBlocks.keySet().size());
        for (Map.Entry<Hairdresser, Set<Block>> entry : freeBlocks.entrySet()) {
            HairdresserDTO hairdresser = new HairdresserDTO(entry.getKey());
            Set<BlockDTO> blocks = getBlockDTOs(entry.getValue());
            HairdresserAvailableBlocks hairdresserAvailableBlocks = new HairdresserAvailableBlocks(hairdresser, blocks);
            availableBlocks.add(hairdresserAvailableBlocks);
        }
        return availableBlocks;
    }

    private Set<BlockDTO> getBlockDTOs(Set<Block> blocks) {
        Set<BlockDTO> blockDTOs = Sets.newTreeSet();
        blocks.stream().forEach(block -> blockDTOs.add(new BlockDTO(block)));
        return blockDTOs;
    }
}
