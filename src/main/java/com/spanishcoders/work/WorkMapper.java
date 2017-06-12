package com.spanishcoders.work;

import java.util.Set;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkMapper {

	@Mapping(source = "kind", target = "workKind")
	WorkDTO asDTO(Work work);

	@InheritInverseConfiguration
	Work asEntity(WorkDTO dto);

	Set<WorkDTO> asDTOs(Set<Work> works);
}
