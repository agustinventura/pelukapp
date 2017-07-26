package com.spanishcoders.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalTime;

import org.junit.Test;

import com.spanishcoders.workingday.block.Block;

/**
 * Created by agustin on 5/07/16.
 */
public class BlockTests {

	@Test
	public void isContiguousToNull() throws Exception {
		final Block block = new Block();
		assertThat(block.isContiguousTo(null), is(false));
	}

	@Test
	public void isContiguousToEmptyBlock() throws Exception {
		final Block block = new Block();
		assertThat(block.isContiguousTo(new Block()), is(false));
	}

	@Test
	public void isContiguousTo() throws Exception {
		final Block block = new Block();
		block.setStart(LocalTime.now());
		final Block contiguousBlock = new Block();
		contiguousBlock.setStart(block.getStart().plusMinutes(Block.BLOCK_MINUTES));
		assertThat(block.isContiguousTo(contiguousBlock), is(true));
	}

	@Test
	public void isNotContiguousTo() throws Exception {
		final Block block = new Block();
		block.setStart(LocalTime.now());
		final Block contiguousBlock = new Block();
		contiguousBlock.setStart(LocalTime.now().plusMinutes(Block.BLOCK_MINUTES).plusMinutes(Block.BLOCK_MINUTES));
		assertThat(block.isContiguousTo(contiguousBlock), is(false));
	}
}