package com.spanishcoders.appointment;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

public class AppointmentDTO implements Comparable<AppointmentDTO> {

	private final Integer id;
	private final Set<Integer> blocks;
	private final Set<Integer> works;
	private final Integer user;
	private final LocalDateTime date;
	private final Long duration;
	private final AppointmentStatus status;
	private final String notes;

	public AppointmentDTO() {
		id = 0;
		user = 0;
		date = null;
		duration = 0L;
		status = AppointmentStatus.CANCELLED;
		notes = "";
		blocks = Sets.newTreeSet();
		works = Sets.newTreeSet();
	}

	public AppointmentDTO(Appointment appointment) {
		id = appointment.getId();
		user = appointment.getUser() != null ? appointment.getUser().getId() : null;
		status = appointment.getStatus();
		blocks = Sets.newTreeSet();
		date = appointment.getDate();
		if (appointment.getBlocks() != null && !appointment.getBlocks().isEmpty()) {
			blocks.addAll(appointment.getBlocks().stream().map(block -> block.getId()).collect(Collectors.toSet()));
		}
		works = Sets.newTreeSet();
		if (appointment.getWorks() != null && !appointment.getWorks().isEmpty()) {
			works.addAll(appointment.getWorks().stream().map(work -> work.getId()).collect(Collectors.toSet()));
			final Long worksLength = appointment.getWorks().stream().mapToLong(work -> work.getDuration().toMinutes())
					.sum();
			duration = worksLength;
		} else {
			duration = 0L;
		}
		notes = appointment.getNotes();
	}

	public Integer getId() {
		return id;
	}

	public Set<Integer> getBlocks() {
		return blocks;
	}

	public Set<Integer> getWorks() {
		return works;
	}

	public Integer getUser() {
		return user;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public Long getDuration() {
		return duration;
	}

	public AppointmentStatus getStatus() {
		return status;
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

		final AppointmentDTO that = (AppointmentDTO) o;

		return id != null ? id.equals(that.id) : that.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "AppointmentDTO{" + "id=" + id + ", blocks=" + blocks + ", works=" + works + ", user=" + user
				+ ", date='" + date + '\'' + ", duration='" + duration + '\'' + ", status=" + status + ", notes="
				+ notes + '}';
	}

	@Override
	public int compareTo(AppointmentDTO o) {
		return this.date.compareTo(o.getDate());
	}
}
