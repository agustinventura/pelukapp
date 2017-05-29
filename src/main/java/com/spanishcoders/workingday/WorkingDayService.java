package com.spanishcoders.workingday;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
