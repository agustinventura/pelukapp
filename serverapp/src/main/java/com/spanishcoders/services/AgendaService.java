package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkingDay;
import com.spanishcoders.repositories.AgendaRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by agustin on 28/06/16.
 */
@Service
public class AgendaService {

    private AgendaRepository agendaRepository;

    public AgendaService(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    public Set<Block> getFirstTenAvailableBlocks(Agenda agenda, Set<Work> works) {
        Set<Block> availableBlocks = Sets.newHashSet();
        if (agenda != null && works != null && !works.isEmpty()) {
            availableBlocks = agenda.getFirstTenAvailableBlocks(works);
            while (availableBlocks.size() < 10) {
                WorkingDay workingDay = new WorkingDay(agenda);
                availableBlocks.addAll(workingDay.getAvailableBlocks(works));
                agendaRepository.save(agenda);
            }
        }
        return availableBlocks;
    }
}
