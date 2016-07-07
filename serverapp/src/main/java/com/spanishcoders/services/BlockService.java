package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.repositories.BlockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Set<Block> get(Set<Integer> blockIds) {
        Set<Block> blocks = Sets.newHashSet();
        if (blockIds != null && !blockIds.isEmpty()) {
            blocks = Sets.newHashSet(blockRepository.findAll(blockIds));
        }
        return blocks;
    }
}
