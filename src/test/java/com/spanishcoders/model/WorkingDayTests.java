package com.spanishcoders.model;

import static com.spanishcoders.TestDataFactory.hairdresser;
import static com.spanishcoders.TestDataFactory.publicWork;
import static com.spanishcoders.TestDataFactory.workingDay;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

public class WorkingDayTests {

	@Test
	public void hasAppointmentWorkingDayWithAppointment() {
		final WorkingDay workingDay = workingDay();
		final Block block = workingDay.getBlocks().iterator().next();
		final Appointment appointment = new Appointment(hairdresser(), publicWork(), Sets.newHashSet(block), "");
		assertThat(workingDay.hasAppointment(), is(true));
	}

	@Test
	public void hasAppointmentWorkingDayWithoutAppointment() {
		final WorkingDay workingDay = workingDay();
		final Block block = workingDay.getBlocks().iterator().next();
		assertThat(workingDay.hasAppointment(), is(false));
	}
}