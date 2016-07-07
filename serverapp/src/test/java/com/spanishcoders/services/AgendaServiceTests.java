package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.repositories.AgendaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * Created by agustin on 2/07/16.
 */
@RunWith(SpringRunner.class)
public class AgendaServiceTests {

    @MockBean
    private AgendaRepository agendaRepository;

    @MockBean
    private WorkingDayService workingDayService;

    private AgendaService agendaService;

    @Before
    public void setUp() {
        agendaService = new AgendaService(agendaRepository, workingDayService);
    }

    @Test
    public void getFirstTenAvailableBlocksWithoutAgenda() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(null, null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksWithoutWorks() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(mockAgenda(), null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksWithEmptyAgenda() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(mockAgenda(), null);
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksWithEmptyWorks() {
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(mockAgenda(), Sets.newHashSet());
        assertThat(blocks.size(), is(0));
    }

    @Test
    public void getFirstTenAvailableBlocksForOneWork() {
        Agenda agenda = Mockito.mock(Agenda.class);
        given(workingDayService.getFirstTenAvailableBlocks(any(Agenda.class), any(Set.class))).willReturn(mockTenBlocks());
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(agenda, mockPublicWork());
        assertThat(blocks.size(), is(10));
    }

    @Test
    public void getFirstTenAvailableBlocksForAllPublicWorks() {
        Agenda agenda = Mockito.mock(Agenda.class);
        given(workingDayService.getFirstTenAvailableBlocks(any(Agenda.class), any(Set.class))).willReturn(mockTenBlocks());
        Set<Block> blocks = agendaService.getFirstTenAvailableBlocks(agenda, mockPublicWorks());
        assertThat(blocks.size(), is(10));
    }
}