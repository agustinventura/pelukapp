package com.spanishcoders.user.hairdresser;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.schedule.Schedule;

@Service
@Transactional(readOnly = true)
public class HairdresserService {

	private final AgendaService agendaService;

	private final UserService userService;

	private final HairdresserRepository hairdresserRepository;

	public HairdresserService(UserService userService, AgendaService agendaService,
			HairdresserRepository hairdresserRepository) {
		this.userService = userService;
		this.agendaService = agendaService;
		this.hairdresserRepository = hairdresserRepository;
	}

	@Transactional(readOnly = false)
	public Hairdresser registerHairdresser(Authentication authentication, Hairdresser hairdresser) {
		if (authentication == null) {
			throw new AccessDeniedException("AppUser needs to be logged to register a hairdresser");
		} else {
			if (hairdresser == null) {
				throw new IllegalArgumentException("Can't create a hairdresser from null data");
			} else {
				if (!authentication.getAuthorities().stream()
						.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()))) {
					// normal user registering hairdresser? not gonna happen
					throw new AccessDeniedException("You need to be worker");
				} else {
					final AppUser user = userService.create(hairdresser);
					hairdresser = hairdresserRepository.findOne(user.getId());
				}
			}
		}
		return hairdresser;
	}

	Set<Schedule> getSchedule(LocalDate day) {
		final Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
		return getBlocks(hairdressers, day);
	}

	private Set<Schedule> getBlocks(Set<Hairdresser> hairdressers, LocalDate day) {
		final Set<Schedule> schedules = Sets.newHashSet();
		for (final Hairdresser hairdresser : hairdressers) {
			final Set<Block> hairdresserBlocks = agendaService.getDayBlocks(hairdresser.getAgenda(), day);
			schedules.add(new Schedule(hairdresser, day, hairdresserBlocks));
		}
		return schedules;
	}
}
