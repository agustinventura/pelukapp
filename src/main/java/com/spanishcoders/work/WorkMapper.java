package com.spanishcoders.work;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkMapper {

	@Mapping(source = "kind", target = "workKind")
	WorkDTO asDTO(Work work);

	Set<WorkDTO> asDTOs(Set<Work> works);
	
	Set<Integer> asIntegers (Set<Work> works);
	
	default Integer asInteger (Work work) {
		return work.getId();
	}
	
	default Work asWork (Integer id) {
		Work work = new Work();
		work.setId(id);
		return work;
	}
	
	Set<Work> asWorks (Set<Integer> ids);
}
