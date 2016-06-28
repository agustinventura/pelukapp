package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.*;
import com.spanishcoders.repositories.HairdresserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

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
    private WorkService workService;

    private HairdresserService hairdresserService;

    @Before
    public void setUp() throws Exception {
        hairdresserService = new HairdresserService(hairdresserRepository, workService);
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
        Hairdresser hairdresser = mockFindHairdresserByStatus();
        mockGetFirstTenAvailableBlocks();
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getFirstTenAvailableBlocksByHairdresser(Sets.newHashSet(cut));
        assertThat(availableBlocks.entrySet(), not((empty())));
        assertThat(availableBlocks.get(hairdresser).size(), is((10)));
    }

    private void mockGetFirstTenAvailableBlocks() {
        Set<Block> blocks = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            blocks.add(new Block());
        }
        given(workService.getFirstTenAvailableBlocks(any(Hairdresser.class), any(Set.class))).willReturn(blocks);
    }

    private Hairdresser mockFindHairdresserByStatus() {
        Hairdresser hairdresser = new Hairdresser("admin", "admin", "phone");
        given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
        return hairdresser;
    }
}