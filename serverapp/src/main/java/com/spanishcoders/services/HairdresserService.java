package com.spanishcoders.services;

import com.google.common.collect.Maps;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.HairdresserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return populateTodaysBlocks();
    }

    private Map<Hairdresser, Set<Block>> populateAvailableBlocks(Set<Work> works) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserAvailableBlocks = agendaService.getFirstTenAvailableBlocks(hairdresser.getAgenda(), works);
            hairdresser.getAppointments().size();
            availableBlocks.put(hairdresser, hairdresserAvailableBlocks);
        }
        return availableBlocks;
    }

    private Map<Hairdresser, Set<Block>> populateTodaysBlocks() {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserBlocks = agendaService.getTodaysBlocks(hairdresser.getAgenda());
            hairdresser.getAppointments().size();
            availableBlocks.put(hairdresser, hairdresserBlocks);
        }
        return availableBlocks;
    }
}
