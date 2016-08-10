package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by agustin on 10/08/16.
 */
public class HairdresserAvailableBlocks {

    private final HairdresserDTO hairdresser;

    private final Set<BlockDTO> availableBlocks;

    public HairdresserAvailableBlocks() {
        hairdresser = new HairdresserDTO();
        availableBlocks = Sets.newHashSet();
    }

    public HairdresserAvailableBlocks(HairdresserDTO hairdresser, Set<BlockDTO> availableBlocks) {
        this.hairdresser = hairdresser;
        this.availableBlocks = availableBlocks;
    }

    public HairdresserDTO getHairdresser() {
        return hairdresser;
    }

    public Set<BlockDTO> getAvailableBlocks() {
        return availableBlocks;
    }
}
