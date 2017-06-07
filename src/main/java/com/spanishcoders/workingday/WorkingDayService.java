package com.spanishcoders.workingday;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkingDayService {

	private final WorkingDayRepository workingDayRepository;

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
