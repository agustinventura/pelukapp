package com.spanishcoders.appointment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import com.google.common.collect.Sets;

public class AppointmentDTO implements Comparable<AppointmentDTO> {

	private Integer id;
	private Set<Integer> blocks;
	private Set<Integer> works;
	private Integer user;
	private LocalDateTime date;
	private Duration duration;
	private AppointmentStatus status;
	private String notes;

	public AppointmentDTO() {
		id = 0;
		user = 0;
		date = null;
		duration = null;
		status = AppointmentStatus.CANCELLED;
		notes = "";
		blocks = Sets.newTreeSet();
		works = Sets.newTreeSet();
	}

	public AppointmentDTO(Integer id, Set<Integer> blocks, Set<Integer> works, Integer user, LocalDateTime date,
			Duration duration, AppointmentStatus status, String notes) {
		super();
		this.id = id;
		this.blocks = blocks;
		this.works = works;
		this.user = user;
		this.date = date;
		this.duration = duration;
		this.status = status;
		this.notes = notes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Integer> blocks) {
		this.blocks = blocks;
	}

	public Set<Integer> getWorks() {
		return works;
	}

	public void setWorks(Set<Integer> works) {
		this.works = works;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
		return "AppointmentDTO [id=" + id + ", blocks=" + blocks + ", works=" + works + ", user=" + user + ", date="
				+ date + ", duration=" + duration + ", status=" + status + ", notes=" + notes + "]";
	}

	@Override
	public int compareTo(AppointmentDTO o) {
		return this.date.compareTo(o.getDate());
	}
}
