package com.spanishcoders.agenda;

import org.mapstruct.Mapper;

@Mapper
public interface AgendaMapper {

	default Integer asInteger(Agenda agenda) {
		Integer agendaId = null;
		if (agenda != null) {
			agendaId = agenda.getId();
		}
		return agendaId;
	}

	default Agenda asAgenda(Integer id) {
		Agenda agenda = null;
		if (id != null) {
			agenda = new Agenda();
			agenda.setId(id);
		}
		return agenda;
	}
}
