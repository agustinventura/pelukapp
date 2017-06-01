package com.spanishcoders.agenda;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.collect.Sets;

@Entity
public class Timetable {

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	private LocalDate startDay;

	@NotNull
	private LocalDate endDay;

	@NotEmpty
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	private Set<Stretch> stretches;

	public Timetable() {
		this.stretches = Sets.newHashSet();
	}

	public Timetable(LocalDate startDay, LocalDate endDay, Stretch... stretchs) {
		this();
		this.startDay = startDay;
		this.endDay = endDay;
		this.stretches.addAll(Arrays.asList(stretchs));
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getStartDay() {
		return startDay;
	}

	public void setStartDay(LocalDate startDay) {
		this.startDay = startDay;
	}

	public LocalDate getEndDay() {
		return endDay;
	}

	public void setEndDay(LocalDate endDay) {
		this.endDay = endDay;
	}

	public Set<Stretch> getStretches() {
		return stretches;
	}

	public void setStretches(Set<Stretch> stretches) {
		this.stretches = stretches;
	}

	public void addStretch(Stretch stretch) {
		this.stretches.add(stretch);
	}

	@Override
	public String toString() {
		return "Timetable{" + "id=" + id + ", startDay=" + startDay + ", endDay=" + endDay + ", stretches=" + stretches
				+ '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDay == null) ? 0 : endDay.hashCode());
		result = prime * result + ((startDay == null) ? 0 : startDay.hashCode());
		result = prime * result + ((stretches == null) ? 0 : stretches.hashCode());
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
		final Timetable other = (Timetable) obj;
		if (endDay == null) {
			if (other.endDay != null) {
				return false;
			}
		} else if (!endDay.equals(other.endDay)) {
			return false;
		}
		if (startDay == null) {
			if (other.startDay != null) {
				return false;
			}
		} else if (!startDay.equals(other.startDay)) {
			return false;
		}
		if (stretches == null) {
			if (other.stretches != null) {
				return false;
			}
		} else if (!stretches.equals(other.stretches)) {
			return false;
		}
		return true;
	}

}
