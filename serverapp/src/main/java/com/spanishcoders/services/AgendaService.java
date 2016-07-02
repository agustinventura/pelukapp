package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkingDay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Created by agustin on 28/06/16.
 */
@Service
public class AgendaService {

    public Set<Block> getFirstTenAvailableBlocks(Agenda agenda, Set<Work> works) {
        Set<Block> availableBlocks = Sets.newHashSet();
        if (agenda != null && works != null && !works.isEmpty()) {
            while (availableBlocks.size() < 10) {
                for (Map.Entry<LocalDate, WorkingDay> entry : agenda.getWorkingDays().entrySet()) {
                    availableBlocks.addAll(entry.getValue().getAvailableBlocks(works));
                }
                if (availableBlocks.size() < 10) {
                    WorkingDay newWorkingDay = new WorkingDay(agenda);
                }
            }
        }
        return availableBlocks;
    }
}
