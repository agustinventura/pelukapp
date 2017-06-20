package com.spanishcoders.user.hairdresser;

import java.time.LocalDate;
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

import com.spanishcoders.workingday.schedule.HairdresserScheduleDTO;

@RestController
@RequestMapping(value = "/hairdresser", produces = MediaType.APPLICATION_JSON_VALUE)
public class HairdresserController {

	private final HairdresserServiceFacade hairdresserServiceFacade;

	public HairdresserController(HairdresserServiceFacade hairdresserServiceFacade) {
		this.hairdresserServiceFacade = hairdresserServiceFacade;
	}

	@PreAuthorize("authenticated")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public HairdresserDTO registerHairdresser(Authentication authentication,
			@RequestBody HairdresserDTO hairdresserDTO) {
		return hairdresserServiceFacade.create(authentication, hairdresserDTO);
	}

	@PreAuthorize("authenticated")
	@RequestMapping(value = "schedule/today", method = RequestMethod.GET)
	public Set<HairdresserScheduleDTO> getTodaySchedule(Authentication authentication) {
		return getSchedules(authentication, LocalDate.now());
	}

	@PreAuthorize("authenticated")
	@RequestMapping(value = "schedule/{day}", method = RequestMethod.GET)
	public Set<HairdresserScheduleDTO> getSchedules(Authentication authentication,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
		return hairdresserServiceFacade.getSchedule(day);
	}
}
