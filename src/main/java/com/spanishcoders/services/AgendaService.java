package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
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

    public AgendaService(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    public Set<Block> getDayBlocks(Agenda agenda, LocalDate day) {
        Set<Block> dayBlocks = Sets.newTreeSet();
        if (agenda != null && day != null) {
            if (agenda.hasWorkingDay(day)) {
                dayBlocks = agenda.getWorkingDayBlocks(day);
            } else {
                if (!agenda.isNonWorkingDay(day)) {
                    new WorkingDay(agenda, day);
                    agendaRepository.save(agenda);
                    dayBlocks = agenda.getWorkingDayBlocks(day);
                }
            }
        }
        return dayBlocks;
    }
}
