package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(SpringRunner.class)
public class HairdresserServiceTests {

    private HairdresserService hairdresserService;

    @Before
    public void setUp() throws Exception {
        hairdresserService = new HairdresserService();
    }

    @Test
    public void getAvailableBlocksForEmptyWorks() {
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(Sets.newHashSet());
        assertThat(availableBlocks.entrySet(), is(empty()));
    }

    @Test
    public void getAvailableBlocksForNullWorks() {
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(null);
        assertThat(availableBlocks.entrySet(), is(empty()));
    }
}