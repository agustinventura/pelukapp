package com.spanishcoders.services;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by agustin on 7/07/16.
 */
public class AppointmentServiceTests {

    private AppointmentService appointmentService;

    @Before
    public void setUp() throws Exception {
        appointmentService = new AppointmentService();
    }

    @Test(expected = IllegalStateException.class)
    public void confirmAppointmentEmptyWorks() throws Exception {
        appointmentService.confirmAppointment(null, Sets.newHashSet(), Sets.newHashSet());
    }

}