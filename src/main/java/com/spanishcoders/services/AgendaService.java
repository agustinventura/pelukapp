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

    public Set<Block> getAvailableBlocks(Agenda agenda, Set<Work> works, LocalDate day) {
        Set<Block> availableBlocks = Sets.newTreeSet();
        if (agenda != null && works != null && !works.isEmpty() && day != null) {
            if (agenda.hasWorkingDay(day)) {
                WorkingDay workingDay = agenda.getWorkingDays().get(day);
                availableBlocks = workingDay.getAvailableBlocks(works);
            } else {
                if (!agenda.isNonWorkingDay(day)) {
                    new WorkingDay(agenda, day);
                    agendaRepository.save(agenda);
                    WorkingDay workingDay = agenda.getWorkingDays().get(day);
                    availableBlocks = workingDay.getAvailableBlocks(works);
                }
            }
        }
        return availableBlocks;
    }
}
