package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.repositories.HairdresserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class HairdresserServiceTests extends PelukaapUnitTest {

    @MockBean
    private HairdresserRepository hairdresserRepository;

    @MockBean
    private AgendaService agendaService;

    private HairdresserService hairdresserService;

    @Before
    public void setUp() throws Exception {
        hairdresserService = new HairdresserService(hairdresserRepository, agendaService);
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

    @Test
    public void getAvailableBlocks() {
        Hairdresser hairdresser = mockHairdresser();
        given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
        given(agendaService.getFirstTenAvailableBlocks(any(Agenda.class), any(Set.class))).willReturn(mockTenBlocks());
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(mockPublicWorks());
        assertThat(availableBlocks.entrySet(), not((empty())));
        assertThat(availableBlocks.get(hairdresser).size(), is((10)));
    }

    @Test
    public void getTodaysBlocks() {
        Hairdresser hairdresser = mock(Hairdresser.class);
        given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
        Set<Block> todaysBlocks = Sets.newHashSet(mock(Block.class));
        given(agendaService.getTodaysBlocks(any(Agenda.class))).willReturn(todaysBlocks);
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getTodaysBlocksByHairdresser();
        assertThat(availableBlocks.entrySet(), not((empty())));
        assertThat(availableBlocks.get(hairdresser).size(), is((todaysBlocks.size())));
    }
}