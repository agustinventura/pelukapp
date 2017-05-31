package com.spanishcoders.user.hairdresser;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.user.UserDTO;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.workingday.block.Block;

@Service
@Transactional
public class HairdresserService {

	private final AgendaService agendaService;

	private final HairdresserRepository hairdresserRepository;

	private final PasswordEncoder passwordEncoder;

	public HairdresserService(HairdresserRepository hairdresserRepository, AgendaService agendaService,
			PasswordEncoder passwordEncoder) {
		this.hairdresserRepository = hairdresserRepository;
		this.agendaService = agendaService;
		this.passwordEncoder = passwordEncoder;
	}

	public Hairdresser registerHairdresser(Authentication authentication, UserDTO userDTO) {
		if (authentication == null) {
			throw new AccessDeniedException("AppUser needs to be logged to register a hairdresser");
		} else {
			if (userDTO == null) {
				throw new IllegalArgumentException("Can't create a hairdresser from null data");
			} else {
				return createHairdresser(userDTO);
			}
		}
	}

	private Hairdresser createHairdresser(UserDTO userDTO) {
		checkUsername(userDTO);
		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		final Hairdresser hairdresser = new Hairdresser(userDTO);
		return hairdresserRepository.save(hairdresser);
	}

	private void checkUsername(UserDTO userDTO) {
		final String username = userDTO.getUsername();
		final Hairdresser hairdresser = hairdresserRepository.findByUsername(username);
		if (hairdresser != null) {
			throw new IllegalArgumentException("There's an user with username " + username);
		}
	}

	Map<Hairdresser, Set<Block>> getDayBlocks(LocalDate day) {
		final Set<Hairdresser> hairdressers = hairdresserRepository.findByStatus(UserStatus.ACTIVE);
		return getDayBlocks(hairdressers, day);
	}

	Map<Hairdresser, Set<Block>> getDayBlocks(Set<Hairdresser> hairdressers, LocalDate day) {
		final Map<Hairdresser, Set<Block>> availableBlocks = Maps.newHashMap();
		for (final Hairdresser hairdresser : hairdressers) {
			final Set<Block> hairdresserBlocks = agendaService.getDayBlocks(hairdresser.getAgenda(), day);
			availableBlocks.put(hairdresser, hairdresserBlocks);
		}
		return availableBlocks;
	}
}
