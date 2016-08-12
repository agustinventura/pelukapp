package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkingDay;
import com.spanishcoders.repositories.WorkingDayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * Created by agustin on 5/07/16.
 */
@Service
@Transactional
public class WorkingDayService {

    private WorkingDayRepository workingDayRepository;

    public WorkingDayService(WorkingDayRepository workingDayRepository) {
        this.workingDayRepository = workingDayRepository;
    }

    public WorkingDay save(WorkingDay workingDay) {
        if (workingDay != null) {
            return workingDayRepository.save(workingDay);
        }
        return workingDay;
    }

    public Set<Block> getFirstTenAvailableBlocks(Agenda agenda, Set<Work> works) {
        Set<Block> availableBlocks = Sets.newTreeSet();
        if (works != null && !works.isEmpty()) {
            SortedSet<WorkingDay> nextWorkingDays = workingDayRepository.getNextWorkingDays(agenda);
            while (availableBlocks.size() < 10) {
                for (WorkingDay workingDay : nextWorkingDays) {
                    availableBlocks.addAll(workingDay.getAvailableBlocks(works));
                }
                if (availableBlocks.size() < 10) {
                    WorkingDay workingDay = new WorkingDay(agenda);
                    workingDay = this.save(workingDay);
                    availableBlocks.addAll(workingDay.getAvailableBlocks(works));
                    if (availableBlocks.size() > 10) {
                        availableBlocks = availableBlocks.stream().limit(10).collect(Collectors.toSet());
                    }
                }
                if (availableBlocks.size() > 10) {
                    availableBlocks = availableBlocks.stream().limit(10).collect(Collectors.toSet());
                }
            }
        }
        return availableBlocks;
    }
}
