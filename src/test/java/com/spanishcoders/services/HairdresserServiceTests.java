package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.*;
import com.spanishcoders.repositories.HairdresserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

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
    public void getTodaysBlocks() {
        Hairdresser hairdresser = mock(Hairdresser.class);
        given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
        Set<Block> todaysBlocks = Sets.newHashSet(mock(Block.class));
        given(agendaService.getDayBlocks(any(Agenda.class), any(LocalDate.class))).willReturn(todaysBlocks);
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getDayBlocks(LocalDate.now());
        assertThat(availableBlocks.entrySet(), not((empty())));
        assertThat(availableBlocks.get(hairdresser).size(), is((todaysBlocks.size())));
    }

    @Test
    public void getAvailableBlocksForDayEmptyWorks() {
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getAvailableBlocksForDayByHairdresser(Sets.newHashSet(), LocalDate.now());
        assertThat(availableBlocks.entrySet(), is(empty()));
    }

    @Test
    public void getAvailableBlocksForDayNullWorks() {
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getAvailableBlocksForDayByHairdresser(null, LocalDate.now());
        assertThat(availableBlocks.entrySet(), is(empty()));
    }

    @Test
    public void getAvailableBlocksForDayNullDate() {
        Set<Work> mockWorks = Sets.newHashSet(mock(Work.class));
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getAvailableBlocksForDayByHairdresser(mockWorks, null);
        assertThat(availableBlocks.entrySet(), is(empty()));
    }

    @Test
    public void getAvailableBlocksForDay() {
        Hairdresser mockHairdresser = mock(Hairdresser.class);
        given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(mockHairdresser));
        Block mockBlock = mock(Block.class);
        Set<Block> blocks = Sets.newHashSet(mockBlock);
        given(agendaService.getAvailableBlocks(any(Agenda.class), any(Set.class), any(LocalDate.class))).willReturn(blocks);
        Set<Work> mockWorks = Sets.newHashSet(mock(Work.class));
        Map<Hairdresser, Set<Block>> returnedBlocks = hairdresserService.getAvailableBlocksForDayByHairdresser(mockWorks, LocalDate.now());
        assertThat(returnedBlocks.size(), is(1));
        assertThat(returnedBlocks.get(mockHairdresser).size(), is(blocks.size()));
    }
}