package com.spanishcoders.user.hairdresser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spanishcoders.workingday.HairdresserScheduleDTO;
import com.spanishcoders.workingday.ScheduleDTO;
import com.spanishcoders.workingday.block.Block;

@RestController
@RequestMapping(value = "/hairdresser", produces = MediaType.APPLICATION_JSON_VALUE)
public class HairdresserController {

	private final HairdresserService hairdresserService;

	public HairdresserController(HairdresserService hairdresserService) {
		this.hairdresserService = hairdresserService;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public HairdresserDTO registerHairdresser(Authentication authentication,
			@RequestBody HairdresserDTO hairdresserDTO) {
		final Hairdresser hairdresser = new Hairdresser(hairdresserDTO);
		return new HairdresserDTO(hairdresserService.registerHairdresser(authentication, hairdresser));
	}

	@PreAuthorize("authenticated")
	@RequestMapping(value = "schedule/today", method = RequestMethod.GET)
	public List<HairdresserScheduleDTO> getTodaySchedule(Authentication authentication) {
		return getDaySchedule(authentication, LocalDate.now());
	}

	@PreAuthorize("authenticated")
	@RequestMapping(value = "schedule/{day}", method = RequestMethod.GET)
	public List<HairdresserScheduleDTO> getDaySchedule(Authentication authentication,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
		final Map<Hairdresser, Set<Block>> todaysBlocks = hairdresserService.getDayBlocks(day);
		return toScheduleDTOs(authentication, todaysBlocks);
	}

	private List<HairdresserScheduleDTO> toScheduleDTOs(Authentication authentication,
			Map<Hairdresser, Set<Block>> todaysBlocks) {
		final List<HairdresserScheduleDTO> schedule = Lists.newArrayList();
		for (final Map.Entry<Hairdresser, Set<Block>> entry : todaysBlocks.entrySet()) {
			final HairdresserDTO hairdresser = new HairdresserDTO(entry.getKey());
			final Set<ScheduleDTO> scheduleDTO = getScheduleDTOs(authentication, entry.getValue());
			schedule.add(new HairdresserScheduleDTO(hairdresser, scheduleDTO));
		}
		return schedule;
	}

	private Set<ScheduleDTO> getScheduleDTOs(Authentication authentication, Set<Block> blocks) {
		final Set<ScheduleDTO> scheduleDTOs = Sets.newTreeSet();
		blocks.stream().forEach(block -> scheduleDTOs.add(new ScheduleDTO(authentication, block)));
		return scheduleDTOs;
	}
}
