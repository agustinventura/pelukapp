package com.spanishcoders.workingday.block;

import java.util.Set;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlockMapper {

	default Integer asInteger (Block block) {
		return block.getId();
	}
	
	Set<Integer> asIntegers (Set<Block> blocks);
}
