package com.spanishcoders.agenda;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "opening_hours")
public class OpeningHours implements Comparable<OpeningHours> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Integer id;

	@NotNull
	private LocalTime startTime;

	@NotNull
	private LocalTime endTime;

	@NotNull
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "opening_hours_opening_day_fk"))
	private OpeningDay openingDay;

	private OpeningHours() {
		this.id = null;
		this.startTime = null;
		this.endTime = null;
		this.openingDay = null;
	}

	public OpeningHours(LocalTime startTime, LocalTime endTime) {
		this();
		this.startTime = startTime;
		this.endTime = endTime;
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

	public OpeningDay getOpeningDay() {
		return openingDay;
	}

	public void setOpeningDay(OpeningDay openingDay) {
		this.openingDay = openingDay;
	}

	@Override
	public String toString() {
		return "OpeningHours [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		final OpeningHours other = (OpeningHours) obj;
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

	@Override
	public int compareTo(OpeningHours o) {
		return this.startTime.compareTo(o.startTime);
	}
}
