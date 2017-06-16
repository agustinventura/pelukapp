package com.spanishcoders.agenda;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgendaMapper {

	default Integer asInteger (Agenda agenda) {
		return agenda.getId();
	}
}
