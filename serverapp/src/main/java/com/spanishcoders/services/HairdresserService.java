package com.spanishcoders.services;

import com.google.common.collect.Maps;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.HairdresserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class HairdresserService {

    private HairdresserRepository hairdresserRepository;

    private WorkService workService;

    public HairdresserService(HairdresserRepository hairdresserRepository, WorkService workService) {
        this.hairdresserRepository = hairdresserRepository;
        this.workService = workService;
    }

    public Map<Hairdresser, Set<Block>> getFirstTenAvailableBlocksByHairdresser(Set<Work> works) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        if (works != null && !works.isEmpty()) {
            availableBlocks = populateAvailableWorks(works);
        }
        return availableBlocks;
    }

    private Map<Hairdresser, Set<Block>> populateAvailableWorks(Set<Work> works) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserAvailableBlocks = workService.getFirstTenAvailableBlocks(hairdresser, works);
            availableBlocks.put(hairdresser, hairdresserAvailableBlocks);
        }
        return availableBlocks;
    }
}
