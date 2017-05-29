package com.spanishcoders.services;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.WorkingDayRepository;
import com.spanishcoders.workingday.WorkingDayService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.spanishcoders.TestDataFactory.mockWorkingDay;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created by agustin on 6/07/16.
 */
public class WorkingDayServiceTests extends PelukaapUnitTest {

    @MockBean
    private WorkingDayRepository workingDayRepository;

    private WorkingDayService workingDayService;

    @Before
    public void setUp() {
        this.workingDayService = new WorkingDayService(workingDayRepository);
    }

    @Test
    public void saveNullWorkingDay() throws Exception {
        assertThat(workingDayService.save(null), nullValue());
    }

    @Test
    public void saveWorkingDay() {
        WorkingDay workingDay = mockWorkingDay();
        given(workingDayRepository.save(workingDay)).willReturn(workingDay);
        assertThat(workingDayService.save(workingDay), is(workingDay));
    }
}