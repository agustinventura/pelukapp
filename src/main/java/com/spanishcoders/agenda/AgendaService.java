package com.spanishcoders.agenda;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

@Service
@Transactional(readOnly = true)
public class AgendaService {

	private final AgendaRepository agendaRepository;

	public AgendaService(AgendaRepository agendaRepository) {
		this.agendaRepository = agendaRepository;
	}

	@Transactional(readOnly = false)
	public Set<Block> getDayBlocks(Agenda agenda, LocalDate day) {
		Set<Block> dayBlocks = Sets.newTreeSet();
		if (agenda != null && day != null) {
			if (agenda.hasWorkingDay(day)) {
				dayBlocks.addAll(agenda.getWorkingDayBlocks(day));
			} else {
				if (!agenda.isClosingDay(day)) {
					new WorkingDay(agenda, day);
					agendaRepository.save(agenda);
					dayBlocks.addAll(agenda.getWorkingDayBlocks(day));
				}
			}
		}
		return dayBlocks;
	}
}
