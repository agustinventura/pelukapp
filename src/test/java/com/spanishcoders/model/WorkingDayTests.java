package com.spanishcoders.model;

import static com.spanishcoders.TestDataFactory.mockWorkingDay;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.time.LocalTime;

import org.junit.Test;

import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

public class WorkingDayTests {

	@Test(expected = IllegalArgumentException.class)
	public void addNullBlock() {
		final WorkingDay workingDay = new WorkingDay();
		workingDay.addBlock(null);
	}

	@Test
	public void addBlock() throws Exception {
		final WorkingDay workingDay = mockWorkingDay();
		final Block block = new Block(LocalTime.now(), workingDay);
		workingDay.addBlock(block);
		assertThat(workingDay.getBlocks(), is(not(empty())));
		assertThat(workingDay.getBlocks(), hasItem(block));
	}
}