package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkingDay;
import com.spanishcoders.repositories.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

/**
 * Created by agustin on 28/06/16.
 */
@Service
@Transactional
public class AgendaService {

    private AgendaRepository agendaRepository;

    private WorkingDayService workingDayService;

    public AgendaService(AgendaRepository agendaRepository, WorkingDayService workingDayService) {
        this.agendaRepository = agendaRepository;
        this.workingDayService = workingDayService;
    }

    public Set<Block> getFirstTenAvailableBlocks(Agenda agenda, Set<Work> works) {
        Set<Block> availableBlocks = Sets.newTreeSet();
        if (agenda != null && works != null && !works.isEmpty()) {
            availableBlocks.addAll(workingDayService.getFirstTenAvailableBlocks(agenda, works));
        }
        return availableBlocks;
    }

    public Set<Block> getTodaysBlocks(Agenda agenda) {
        Set<Block> dayBlocks = Sets.newTreeSet();
        if (agenda != null) {
            LocalDate today = LocalDate.now();
            if (agenda.hasWorkingDay(today)) {
                dayBlocks = agenda.getWorkingDayBlocks(today);
            } else {
                if (!agenda.isNonWorkingDay(today)) {
                    new WorkingDay(agenda, today);
                    agendaRepository.save(agenda);
                    dayBlocks = agenda.getWorkingDayBlocks(today);
                }
            }
        }
        return dayBlocks;
    }
}
