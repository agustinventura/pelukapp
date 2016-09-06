package com.spanishcoders.services;

import com.google.common.collect.Maps;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.HairdresserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class HairdresserService {

    private AgendaService agendaService;

    private HairdresserRepository hairdresserRepository;

    public HairdresserService(HairdresserRepository hairdresserRepository, AgendaService agendaService) {
        this.hairdresserRepository = hairdresserRepository;
        this.agendaService = agendaService;
    }

    public Map<Hairdresser, Set<Block>> getFirstTenAvailableBlocksByHairdresser(Set<Work> works) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        if (works != null && !works.isEmpty()) {
            availableBlocks = populateAvailableBlocks(works);
        }
        return availableBlocks;
    }

    public Map<Hairdresser, Set<Block>> getTodaysBlocksByHairdresser() {
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        return getTodayHairdresserBlocks(hairdressers);
    }

    private Map<Hairdresser, Set<Block>> getTodayHairdresserBlocks(Set<Hairdresser> hairdressers) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserBlocks = agendaService.getTodaysBlocks(hairdresser.getAgenda());
            availableBlocks.put(hairdresser, hairdresserBlocks);
        }
        return availableBlocks;
    }

    private Map<Hairdresser, Set<Block>> populateAvailableBlocks(Set<Work> works) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserAvailableBlocks = agendaService.getFirstTenAvailableBlocks(hairdresser.getAgenda(), works);
            availableBlocks.put(hairdresser, hairdresserAvailableBlocks);
        }
        return availableBlocks;
    }

    public Map<Hairdresser, Set<Block>> getAvailableBlocksForDayByHairdresser(Set<Work> works, LocalDate day) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        if (works != null && !works.isEmpty() && day != null) {
            availableBlocks = populateAvailableBlocks(works, day);
        }
        return availableBlocks;
    }

    private Map<Hairdresser, Set<Block>> populateAvailableBlocks(Set<Work> works, LocalDate day) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserAvailableBlocks = agendaService.getAvailableBlocks(hairdresser.getAgenda(), works, day);
            availableBlocks.put(hairdresser, hairdresserAvailableBlocks);
        }
        return availableBlocks;
    }
}