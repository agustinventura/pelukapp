package com.spanishcoders.workingday.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.google.common.collect.Sets;

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
