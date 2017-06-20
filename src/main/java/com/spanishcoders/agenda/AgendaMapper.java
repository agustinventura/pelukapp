package com.spanishcoders.agenda;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgendaMapper {

	default Integer asInteger (Agenda agenda) {
		return agenda.getId();
	}
	
	default Agenda asAgenda (Integer id) {
		Agenda agenda = new Agenda();
		agenda.setId(id);
		return agenda;
	}
}
