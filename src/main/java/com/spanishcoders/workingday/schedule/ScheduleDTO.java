package com.spanishcoders.workingday.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;

import com.google.common.collect.Sets;
import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.user.Role;
import com.spanishcoders.workingday.block.Block;

public class ScheduleDTO implements Comparable<ScheduleDTO> {

	private final Integer blockId;

	private final LocalTime start;

	private final Duration length;

	private final LocalDate workingDay;

	private final Integer hairdresserId;

	private final Integer appointmentId;

	private final String client;

	private final Set<Integer> worksIds;

	private final String notes;

	public ScheduleDTO() {
		blockId = 0;
		start = null;
		length = null;
		workingDay = null;
		hairdresserId = 0;
		appointmentId = 0;
		client = "";
		worksIds = Sets.newHashSet();
		notes = "";
	}

	public ScheduleDTO(Integer blockId, LocalTime start, Duration length, LocalDate workingDay, Integer hairdresserId,
			Integer appointmentId, String client, Set<Integer> worksIds, String notes) {
		super();
		this.blockId = blockId;
		this.start = start;
		this.length = length;
		this.workingDay = workingDay;
		this.hairdresserId = hairdresserId;
		this.appointmentId = appointmentId;
		this.client = client;
		this.worksIds = worksIds;
		this.notes = notes;
	}



	public ScheduleDTO(Authentication authentication, Block block) {
		blockId = block.getId();
		start = block.getStart();
		length = block.getLength();
		workingDay = block.getWorkingDay() != null ? block.getWorkingDay().getDate() : null;
		hairdresserId = block.getWorkingDay().getAgenda().getHairdresser().getId();
		appointmentId = block.getAppointment() != null ? block.getAppointment().getId() : 0;
		if (isWorker(authentication) || isProprietary(authentication, block.getAppointment())) {
			client = block.getAppointment() != null ? block.getAppointment().getUser().getName() : "";
			worksIds = Sets.newHashSet();
			if (block.getAppointment() != null) {
				worksIds.addAll(block.getAppointment().getWorks().stream().map(work -> work.getId())
						.collect(Collectors.toSet()));
			}
			notes = block.getAppointment() != null ? block.getAppointment().getNotes() : "";
		} else {
			client = "";
			worksIds = Sets.newHashSet();
			notes = "";
		}
	}

	private boolean isProprietary(Authentication authentication, Appointment appointment) {
		boolean isProprietary = false;
		if (authentication != null && appointment != null) {
			isProprietary = authentication.getName().equals(appointment.getUser().getName());
		}
		return isProprietary;
	}

	private boolean isWorker(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()));
	}

	public Integer getBlockId() {
		return blockId;
	}

	public LocalTime getStart() {
		return start;
	}

	public Duration getLength() {
		return length;
	}

	public LocalDate getWorkingDay() {
		return workingDay;
	}

	public Integer getHairdresserId() {
		return hairdresserId;
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public String getClient() {
		return client;
	}

	public Set<Integer> getWorksIds() {
		return worksIds;
	}

	public String getNotes() {
		return notes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final ScheduleDTO that = (ScheduleDTO) o;

		return blockId != null ? blockId.equals(that.blockId) : that.blockId == null;

	}

	@Override
	public int hashCode() {
		return blockId != null ? blockId.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "ScheduleDTO [blockId=" + blockId + ", start=" + start + ", length=" + length + ", workingDay="
				+ workingDay + ", hairdresserId=" + hairdresserId + ", appointmentId=" + appointmentId + ", client="
				+ client + ", worksIds=" + worksIds + ", notes=" + notes + "]";
	}

	@Override
	public int compareTo(ScheduleDTO o) {
		if (start.equals(o.getStart())) {
			return workingDay.compareTo(o.getWorkingDay());
		} else {
			return start.compareTo(o.getStart());
		}
	}
}