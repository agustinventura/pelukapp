package com.spanishcoders.work;

import java.util.Set;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WorkMapper {

	@Mappings({ @Mapping(source = "kind", target = "workKind"), @Mapping(source = "status", target = "workStatus") })
	WorkDTO asDTO(Work work);
	
	@InheritInverseConfiguration
	Work asWork (WorkDTO dto);

	Set<WorkDTO> asDTOs(Set<Work> works);

	Set<Integer> asIntegers(Set<Work> works);

	default Integer asInteger(Work work) {
		return work.getId();
	}

	default Work asWork(Integer id) {
		Work work = new Work();
		work.setId(id);
		return work;
	}

	Set<Work> asWorks(Set<Integer> ids);
}
