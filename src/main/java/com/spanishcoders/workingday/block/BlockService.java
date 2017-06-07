package com.spanishcoders.workingday.block;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

@Service
@Transactional(readOnly = true)
public class BlockService {

	private final BlockRepository blockRepository;

	public BlockService(BlockRepository blockRepository) {
		this.blockRepository = blockRepository;
	}

	public Set<Block> get(Collection<Integer> blockIds) {
		Set<Block> blocks = Sets.newHashSet();
		if (blockIds != null && !blockIds.isEmpty()) {
			blocks = Sets.newTreeSet(blockRepository.findAll(blockIds));
		}
		return blocks;
	}
}
