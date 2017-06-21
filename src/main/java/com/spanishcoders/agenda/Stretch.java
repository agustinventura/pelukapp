package com.spanishcoders.agenda;

import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "stretch")
public class Stretch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Integer id;

	@NotNull
	private final LocalTime startTime;

	@NotNull
	private final LocalTime endTime;

	private final DayOfWeek dayOfWeek;

	public Stretch() {
		id = null;
		startTime = null;
		endTime = null;
		dayOfWeek = null;
	}

	public Stretch(LocalTime startTime, LocalTime endTime) {
		this.id = null;
		this.startTime = startTime;
		this.endTime = endTime;
		dayOfWeek = null;
	}

	public Stretch(LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {
		this.id = null;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayOfWeek = dayOfWeek;
	}

	public Integer getId() {
		return id;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	@Override
	public String toString() {
		return "Stretch [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", dayOfWeek=" + dayOfWeek
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Stretch other = (Stretch) obj;
		if (dayOfWeek != other.dayOfWeek) {
			return false;
		}
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!endTime.equals(other.endTime)) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		return true;
	}

}
