package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.AgendaRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by agustin on 28/06/16.
 */
@Service
public class AgendaService {

    private AgendaRepository agendaRepository;

    private WorkingDayService workingDayService;

    public AgendaService(AgendaRepository agendaRepository, WorkingDayService workingDayService) {
        this.agendaRepository = agendaRepository;
        this.workingDayService = workingDayService;
    }

    public Set<Block> getFirstTenAvailableBlocks(Agenda agenda, Set<Work> works) {
        Set<Block> availableBlocks = Sets.newHashSet();
        if (agenda != null && works != null && !works.isEmpty()) {
            availableBlocks = workingDayService.getFirstTenAvailableBlocks(agenda, works);
        }
        return availableBlocks;
    }
}
