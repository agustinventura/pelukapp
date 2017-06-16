package com.spanishcoders.user.client;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

	ClientDTO asDTO(Client client);
}
