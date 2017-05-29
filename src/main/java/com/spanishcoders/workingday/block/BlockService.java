package com.spanishcoders.workingday.block;

import com.google.common.collect.Sets;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

/**
 * Created by agustin on 7/07/16.
 */
@Service
@Transactional
public class BlockService {

    private BlockRepository blockRepository;

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
