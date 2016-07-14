package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.WorkingDay;
import com.spanishcoders.repositories.BlockRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * Created by agustin on 7/07/16.
 */
@RunWith(SpringRunner.class)
public class BlockServiceTests {

    @MockBean
    private BlockRepository blockRepository;

    private BlockService blockService;

    @Before
    public void setUp() throws Exception {
        blockService = new BlockService(blockRepository);
    }

    @Test
    public void getBlocksById() {
        given(blockRepository.findAll(any(Set.class))).will(invocation -> {
            Set<Integer> blockIds = (Set<Integer>) invocation.getArguments()[0];
            Set<Block> blocks = blockIds.stream().map(id -> {
                Block block = new Block();
                block.setId(id);
                block.setStart(LocalTime.now().plusHours(id));
                WorkingDay workingDay = new WorkingDay();
                workingDay.setDate(LocalDate.now());
                block.setWorkingDay(workingDay);
                return block;
            }).collect(Collectors.toSet());
            return blocks;
        });
        Set<Block> blocks = blockService.get(Sets.newHashSet(1, 2));
        assertThat(blocks, hasSize(2));
    }

    @Test
    public void getBlocksByEmptyId() {
        assertThat(blockService.get(Sets.newHashSet()), is(empty()));
    }

    @Test
    public void getBlocksByNullId() {
        assertThat(blockService.get(Sets.newHashSet()), is(empty()));
    }

}