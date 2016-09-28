package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by agustin on 10/08/16.
 */
public class HairdresserBlocks {

    private final HairdresserDTO hairdresser;

    private final Set<BlockDTO> blocks;

    public HairdresserBlocks() {
        hairdresser = new HairdresserDTO();
        blocks = Sets.newHashSet();
    }

    public HairdresserBlocks(HairdresserDTO hairdresser, Set<BlockDTO> blocks) {
        this.hairdresser = hairdresser;
        this.blocks = blocks;
    }

    public HairdresserDTO getHairdresser() {
        return hairdresser;
    }

    public Set<BlockDTO> getBlocks() {
        return blocks;
    }
}
