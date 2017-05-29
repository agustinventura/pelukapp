package com.spanishcoders.model;

import org.junit.Test;

import com.spanishcoders.workingday.block.Block;

import java.time.LocalTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by agustin on 5/07/16.
 */
public class BlockTests {

    @Test
    public void isContiguousToNull() throws Exception {
        Block block = new Block();
        assertThat(block.isContiguousTo(null), is(false));
    }

    @Test
    public void isContiguousToEmptyBlock() throws Exception {
        Block block = new Block();
        assertThat(block.isContiguousTo(new Block()), is(false));
    }

    @Test
    public void isContiguousTo() throws Exception {
        Block block = new Block();
        block.setStart(LocalTime.now());
        Block contiguousBlock = new Block();
        contiguousBlock.setStart(block.getStart().plus(Block.DEFAULT_BLOCK_LENGTH));
        assertThat(block.isContiguousTo(contiguousBlock), is(true));
    }

    @Test
    public void isNotContiguousTo() throws Exception {
        Block block = new Block();
        block.setStart(LocalTime.now());
        Block contiguousBlock = new Block();
        contiguousBlock.setStart(LocalTime.now().plus(Block.DEFAULT_BLOCK_LENGTH).plus(Block.DEFAULT_BLOCK_LENGTH));
        assertThat(block.isContiguousTo(contiguousBlock), is(false));
    }
}