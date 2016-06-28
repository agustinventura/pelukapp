package com.spanishcoders;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static com.spanishcoders.model.Block.DEFAULT_BLOCK_LENGTH;

/**
 * Created by agustin on 28/06/16.
 */
public class TestDataFactory {

    public static Set<Block> mockBlocks() {
        LocalTime startTime = LocalTime.of(9, 00);
        Set<Block> testBlocks = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            startTime = startTime.plus(DEFAULT_BLOCK_LENGTH);
            testBlocks.add(new Block(startTime, null));
        }
        return testBlocks;
    }

    public static Set<Work> mockPrivateWorks() {
        Work regulation = new Work("Regulacion", 30, WorkKind.PRIVATE);
        return Sets.newHashSet(regulation);
    }

    public static Set<Work> mockPublicWorks() {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        Work shave = new Work("Afeitado", 30, WorkKind.PUBLIC);
        return Sets.newHashSet(cut, shave);
    }

    public static Set<Work> mockAllWorks() {
        return Sets.union(mockPublicWorks(), mockPrivateWorks());
    }

    public static Map<Hairdresser, Set<Block>> mockBlocksByHairdresser() {
        Map<Hairdresser, Set<Block>> availableBlocksByHairDresser = Maps.newHashMap();
        availableBlocksByHairDresser.put(mockHairdresser(), mockBlocks());
        return availableBlocksByHairDresser;
    }

    public static Hairdresser mockHairdresser() {
        return new Hairdresser("admin", "admin", "phone");
    }
}
