package com.spanishcoders.user.hairdresser;

import com.google.common.collect.Maps;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.workingday.block.Block;

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

    public Map<Hairdresser, Set<Block>> getDayBlocks(LocalDate day) {
        Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
        return getDayBlocks(hairdressers, day);
    }

    private Map<Hairdresser, Set<Block>> getDayBlocks(Set<Hairdresser> hairdressers, LocalDate day) {
        Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
        for (Hairdresser hairdresser : hairdressers) {
            Set<Block> hairdresserBlocks = agendaService.getDayBlocks(hairdresser.getAgenda(), day);
            availableBlocks.put(hairdresser, hairdresserBlocks);
        }
        return availableBlocks;
    }
}
