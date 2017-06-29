package com.spanishcoders.appointment;

import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockService;

@Component
public class AppointmentServiceFacade {

	private static final Logger logger = Logger.getLogger(AppointmentServiceFacade.class);

	private final AppointmentService appointmentService;

	private final AppointmentMapper appointmentMapper;

	private final BlockService blockService;

	private final WorkService workService;

	private final UserService userService;

	public AppointmentServiceFacade(AppointmentService appointmentService, AppointmentMapper appointmentMapper,
			BlockService blockService, WorkService workService, UserService userService) {
		super();
		this.appointmentService = appointmentService;
		this.appointmentMapper = appointmentMapper;
		this.blockService = blockService;
		this.workService = workService;
		this.userService = userService;
	}

	public AppointmentDTO create(Authentication authentication, AppointmentDTO appointmentDTO) {
		final AppUser user = checkUser(authentication);
		final Set<Block> blocks = blockService.get(appointmentDTO.getBlocks());
		final Set<Work> works = Sets.newHashSet(workService.get(appointmentDTO.getWorks()));
		return appointmentMapper
				.asDTO(appointmentService.createAppointment(user, blocks, works, appointmentDTO.getNotes()));
	}

	private AppUser checkUser(Authentication authentication) {
		final Optional<AppUser> user = authentication != null ? userService.get(authentication.getName())
				: Optional.empty();
		if (!user.isPresent()) {
			logger.error("Can't create an Appointment without AppUser");
			throw new AccessDeniedException("Can't create an Appointment without AppUser");
		}
		return user.get();
	}

	public AppointmentDTO update(Authentication authentication, AppointmentDTO appointment) {
		final AppUser user = checkUser(authentication);
		final Optional<Appointment> original = appointmentService.get(appointment.getId());
		Appointment modified = null;
		if (original.isPresent()) {
			modified = appointmentService.update(user, appointment.getStatus(), appointment.getNotes(), original.get());
		} else {
			logger.error("AppUser " + user.getUsername() + " tried to update non-existing appointment " + appointment);
			throw new IllegalArgumentException("There's no Appointment which matches " + appointment);
		}
		return appointmentMapper.asDTO(modified);
	}

}
