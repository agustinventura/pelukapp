package com.spanishcoders.services;

import static com.spanishcoders.TestDataFactory.workingDay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockRepository;
import com.spanishcoders.workingday.block.BlockService;

public class BlockServiceTests extends PelukaapUnitTest {

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
			final Set<Integer> blockIds = (Set<Integer>) invocation.getArguments()[0];
			final Set<Block> blocks = blockIds.stream().map(id -> {
				final Block block = new Block();
				block.setId(id);
				block.setStart(LocalTime.now().plusHours(id));
				final WorkingDay workingDay = workingDay();
				workingDay.setDate(LocalDate.now());
				block.setWorkingDay(workingDay);
				return block;
			}).collect(Collectors.toSet());
			return blocks;
		});
		final Set<Block> blocks = blockService.get(Sets.newHashSet(1, 2));
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