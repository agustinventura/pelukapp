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
	protected Integer id;

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

	@Override
	public String toString() {
		return "Timetable{" + "id=" + id + ", startDay=" + startDay + ", endDay=" + endDay + ", stretches=" + stretches
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Timetable timetable = (Timetable) o;

		return id != null ? id.equals(timetable.id) : timetable.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public void addStretch(Stretch stretch) {
		this.stretches.add(stretch);
	}
}
