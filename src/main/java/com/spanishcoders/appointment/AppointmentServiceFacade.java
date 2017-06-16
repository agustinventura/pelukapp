package com.spanishcoders.appointment;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AppointmentServiceFacade {

	private final AppointmentService appointmentService;

	private final AppointmentMapper appointmentMapper;

	public AppointmentServiceFacade(AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
		super();
		this.appointmentService = appointmentService;
		this.appointmentMapper = appointmentMapper;
	}

	public AppointmentDTO create(Authentication authentication, AppointmentDTO appointment) {
		return appointmentMapper.asDTO(appointmentService.createAppointment(authentication, appointment));
	}

	public AppointmentDTO update(Authentication authentication, AppointmentDTO appointment) {
		return appointmentMapper.asDTO(appointmentService.update(authentication, appointment));
	}

}
