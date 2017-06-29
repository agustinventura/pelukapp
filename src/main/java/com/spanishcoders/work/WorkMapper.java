package com.spanishcoders.work;

import java.util.Set;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface WorkMapper {

	@Mappings({ @Mapping(source = "kind", target = "workKind"), @Mapping(source = "status", target = "workStatus") })
	WorkDTO asDTO(Work work);

	@InheritInverseConfiguration
	Work asWork(WorkDTO dto);

	Set<WorkDTO> asDTOs(Set<Work> works);

	Set<Integer> asIntegers(Set<Work> works);

	default Integer asInteger(Work work) {
		Integer workId = null;
		if (work != null) {
			workId = work.getId();
		}
		return workId;
	}

	default Work asWork(Integer id) {
		Work work = null;
		if (id != null) {
			work = new Work();
			work.setId(id);
		}
		return work;
	}

	Set<Work> asWorks(Set<Integer> ids);
}
