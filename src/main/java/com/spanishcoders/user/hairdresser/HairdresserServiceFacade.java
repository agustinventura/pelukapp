package com.spanishcoders.user.hairdresser;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.schedule.HairdresserScheduleDTO;
import com.spanishcoders.workingday.schedule.Schedule;
import com.spanishcoders.workingday.schedule.ScheduleMapper;

@Component
public class HairdresserServiceFacade {

	private static final Logger logger = Logger.getLogger(HairdresserServiceFacade.class);

	private final HairdresserService hairdresserService;

	private final UserService userService;

	private final HairdresserMapper hairdresserMapper;

	private final ScheduleMapper scheduleMapper;

	public HairdresserServiceFacade(HairdresserService hairdresserService, UserService userService,
			HairdresserMapper hairdresserMapper, ScheduleMapper scheduleMapper) {
		super();
		this.hairdresserService = hairdresserService;
		this.userService = userService;
		this.hairdresserMapper = hairdresserMapper;
		this.scheduleMapper = scheduleMapper;
	}

	public HairdresserDTO create(Authentication authentication, HairdresserDTO hairdresserDTO) {
		Hairdresser hairdresser = hairdresserMapper.asEntity(hairdresserDTO);
		if (authentication == null) {
			throw new AccessDeniedException("AppUser needs to be logged to register a hairdresser");
		} else {
			final Optional<AppUser> user = userService.get(authentication.getName());
			if (user.isPresent()) {
				hairdresser = hairdresserService.registerHairdresser(user.get(), hairdresser);
			} else {
				logger.error("Unknown user " + authentication.getName() + " tried to register hairdresser "
						+ hairdresserDTO);
				throw new AccessDeniedException("Unknown users can't register clients");
			}
		}
		return hairdresserMapper.asDTO(hairdresser);
	}

	public Set<HairdresserScheduleDTO> getSchedule(Authentication authentication, LocalDate day) {
		final AppUser user = getUser(authentication);
		final Set<Schedule> schedules = hairdresserService.getSchedule(day);
		clearSensitiveData(user, schedules);
		final Set<HairdresserScheduleDTO> dtos = scheduleMapper.toDTOs(schedules);
		return dtos;
	}

	private void clearSensitiveData(AppUser user, Set<Schedule> schedules) {
		if (!user.getRole().equals(Role.WORKER)) {
			for (final Schedule schedule : schedules) {
				for (final Block block : schedule.getBlocks()) {
					final Appointment appointment = block.getAppointment();
					if (appointment != null && !appointment.getUser().equals(user)) {
						appointment.setWorks(null);
						appointment.setUser(null);
						appointment.setNotes(null);
					}
				}
			}
		}
	}

	private AppUser getUser(Authentication authentication) {
		final Optional<AppUser> user = authentication != null ? userService.get(authentication.getName())
				: Optional.empty();
		if (!user.isPresent()) {
			logger.error("Can't access Schedule without AppUser");
			throw new AccessDeniedException("Can't access Schedule without AppUser");
		}
		return user.get();
	}
}
