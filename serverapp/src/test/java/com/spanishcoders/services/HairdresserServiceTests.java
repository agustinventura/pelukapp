package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.repositories.HairdresserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
public class HairdresserServiceTests {

    @MockBean
    private HairdresserRepository hairdresserRepository;

    @MockBean
    private Agenda agenda;

    private HairdresserService hairdresserService;

    @Before
    public void setUp() throws Exception {
        hairdresserService = new HairdresserService(hairdresserRepository);
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
        Hairdresser hairdresser = mockHairdresser(agenda);
        given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
        given(agenda.getFirstTenAvailableBlocks(any(Set.class))).willReturn(mockBlocks());
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(mockPublicWorks());
        assertThat(availableBlocks.entrySet(), not((empty())));
        assertThat(availableBlocks.get(hairdresser).size(), is((10)));
    }


}