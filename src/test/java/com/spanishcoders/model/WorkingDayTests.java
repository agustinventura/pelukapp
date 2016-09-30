package com.spanishcoders.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
        Agenda agenda = mock(Agenda.class);
        given(agenda.getNonWorkingDays()).willReturn(Sets.newHashSet());
        given(agenda.getWorkingDays()).willReturn(Maps.newTreeMap());
        Stretch stretch = mock(Stretch.class);
        int startHour = LocalTime.now().getHour() + 1;
        if (startHour > 23) {
            startHour--;
        }
        int endHour = (startHour + 1);
        if (endHour > 23) {
            endHour = startHour;
        }
        given(stretch.getStartTime()).willReturn(LocalTime.of(startHour, 00));
        given(stretch.getEndTime()).willReturn(LocalTime.of(endHour, 00));
        Timetable timetable = mock(Timetable.class);
        given(timetable.getStretches()).willReturn(Sets.newHashSet(stretch));
        given(agenda.getCurrentTimetable()).willReturn(timetable);
        WorkingDay workingDay = new WorkingDay(agenda);
        Work publicWork = mock(Work.class);
        given(publicWork.getDuration()).willReturn(30);
        given(publicWork.getKind()).willReturn(WorkKind.PUBLIC);
        Set<Work> publicWorks = Sets.newHashSet(publicWork);
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(publicWorks);
        Duration stretchDuration = Duration.ofHours(endHour - startHour);
        int blocksSize = (int) (stretchDuration.toMinutes() / Block.DEFAULT_BLOCK_LENGTH.toMinutes());
        assertThat(availableBlocks.size(), is(blocksSize));
    }

    @Test
    public void getAvailableBlocksRandomFillDayOneWork() {
        WorkingDay workingDay = mockRandomFillWorkingDay();
        Set<Block> availableBlocks = workingDay.getAvailableBlocks(mockPublicWork());
        int blocksWithoutAppointments = (int) workingDay.getBlocks().stream().filter(b -> b.getAppointment() == null
                && b.getStart().isAfter(LocalTime.now())).count();
        assertThat(availableBlocks.size(), is(blocksWithoutAppointments));
    }

}