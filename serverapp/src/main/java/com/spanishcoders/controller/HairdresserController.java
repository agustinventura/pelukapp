package com.spanishcoders.controller;

import com.google.common.collect.Maps;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserDTO;
import com.spanishcoders.services.BlockService;
import com.spanishcoders.services.HairdresserService;
import com.spanishcoders.services.WorkService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    private BlockService blockService;

    public HairdresserController(HairdresserService hairdresserService, WorkService workService, BlockService blockService) {
        this.hairdresserService = hairdresserService;
        this.workService = workService;
        this.blockService = blockService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "blocks/free/{works}", method = RequestMethod.GET)
    public Map<HairdresserDTO, Set<BlockDTO>> getFreeBlocks(Authentication authentication, @MatrixVariable Set<Integer> works) {
        Set<Work> requestedWorks = workService.get(works);
        Map<Hairdresser, Set<Block>> freeBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(requestedWorks);
        return toDTOs(freeBlocks);
    }

    private Map<HairdresserDTO, Set<BlockDTO>> toDTOs(Map<Hairdresser, Set<Block>> freeBlocks) {
        Map<HairdresserDTO, Set<BlockDTO>> freeBlocksDTOs = Maps.newHashMap();
        for (Map.Entry<Hairdresser, Set<Block>> entry : freeBlocks.entrySet()) {
            HairdresserDTO hairdresser = hairdresserService.getHairdresserDTO(entry.getKey());
            Set<BlockDTO> blocks = blockService.getBlockDTOs(entry.getValue());
            freeBlocksDTOs.put(hairdresser, blocks);
        }
        return freeBlocksDTOs;
    }
}
