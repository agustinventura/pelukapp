package com.spanishcoders.model;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by agustin on 4/07/16.
 */
public class WorkingDayTests {

    @Test(expected = IllegalArgumentException.class)
    public void addNullBlock() {
        WorkingDay workingDay = new WorkingDay();
        workingDay.addBlock(null);
    }

    @Test
    public void addBlock() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        Block block = new Block(LocalTime.now(), workingDay);
        workingDay.addBlock(block);
        assertThat(workingDay.getBlocks(), is(not(empty())));
        assertThat(workingDay.getBlocks(), hasItem(block));
    }

    @Test
    public void getAvailableBlocksNullWorks() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(null);
        assertThat(availableBlocks, is(empty()));
    }

    @Test
    public void getAvailableBlocksEmptyWorks() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(Sets.newHashSet());
        assertThat(availableBlocks, is(empty()));
    }

    @Test
    public void getAvailableBlocksEmptyDayOneWork() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(mockPublicWork());
        assertThat(availableBlocks, is(not(empty())));
        assertThat(availableBlocks.size(), is(16));
    }

    @Test
    public void getAvailableBlocksEmptyTwoWorks() throws Exception {
        WorkingDay workingDay = mockWorkingDay();
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(mockPublicWorks());
        assertThat(availableBlocks, is(not(empty())));
        assertThat(availableBlocks.size(), is(14));
    }

    @Test
    public void getAvailableBlocksRandomFillDayOneWork() {
        WorkingDay workingDay = mockRandomFillWorkingDay();
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(mockPublicWork());
        int blocksWithoutAppointments = (int) workingDay.getBlocks().stream().filter(b -> b.getAppointment() == null).count();
        assertThat(availableBlocks.size(), is(blocksWithoutAppointments));
    }

    @Test
    public void getAvailableBlocksRandomFillDayTwoWorks() {
        //TODO: this test seems a little fragile, but couldn't find a fast way to implement it without reimplementing code from WorkingDay
        WorkingDay workingDay = mockWorkingDay();
        workingDay.getBlocks().first().setAppointment(new Appointment());
        workingDay.getBlocks().last().setAppointment(new Appointment());
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(mockPublicWorks());
        assertThat(availableBlocks.size(), is(12));
    }

}