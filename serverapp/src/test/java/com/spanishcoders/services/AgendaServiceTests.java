package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

/**
 * Created by agustin on 28/06/16.
 */
public class AgendaServiceTests {

    private AgendaService agendaService;

    @Before
    public void setUp() {
        agendaService = new AgendaService();
    }

    @Test
    public void getFirstTenAvailableBlocksNullAgenda() {
        Set<Block> availableBlocks = agendaService.getFirstTenAvailableBlocks(null, null);
        assertThat(availableBlocks, is(empty()));
    }

    @Test
    public void getFirstTenAvailableBlocksNullWorks() {
        Set<Block> availableBlocks = agendaService.getFirstTenAvailableBlocks(new Agenda(), null);
        assertThat(availableBlocks, is(empty()));
    }

    @Test
    public void getFirstTenAvailableBlocksEmptyWorks() {
        Set<Block> availableBlocks = agendaService.getFirstTenAvailableBlocks(new Agenda(), Sets.newHashSet());
        assertThat(availableBlocks, is(empty()));
    }


}