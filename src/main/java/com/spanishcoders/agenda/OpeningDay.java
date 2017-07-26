package com.spanishcoders.agenda;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Entity
@Immutable
@Table(name = "opening_day")
public class OpeningDay implements Comparable<OpeningDay> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Integer id;

	@NotNull
	private DayOfWeek weekDay;

	@NotNull
	@OneToMany(mappedBy = "openingDay", cascade = CascadeType.ALL)
	private final Set<OpeningHours> openingHours;

	@NotNull
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "opening_day_timetable_fk"))
	private Timetable timetable;

	private OpeningDay() {
		this.id = null;
		this.weekDay = null;
		this.timetable = null;
		this.openingHours = Sets.newTreeSet();
	}

	OpeningDay(Timetable timetable, DayOfWeek weekDay, OpeningHours... openingHours) {
		this();
		if (weekDay == null) {
			throw new IllegalArgumentException("Can't create and OpeningDay without week day");
		}
		this.weekDay = weekDay;
		if (openingHours == null || openingHours.length == 0) {
			throw new IllegalArgumentException("Can't create and OpeningDay without opening hours");
		}
		if (timetable == null) {
			throw new IllegalArgumentException("Can't create and OpeningDay without timetable");
		}
		this.timetable = timetable;
		this.openingHours.addAll(Arrays.asList(openingHours));
		this.openingHours.stream().forEach(opened -> opened.setOpeningDay(this));
	}

	public Integer getId() {
		return id;
	}

	public DayOfWeek getWeekDay() {
		return weekDay;
	}

	public Set<OpeningHours> getOpeningHours() {
		return ImmutableSet.copyOf(openingHours);
	}

	@Override
	public int compareTo(OpeningDay o) {
		return this.weekDay.compareTo(o.getWeekDay());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((openingHours == null) ? 0 : openingHours.hashCode());
		result = prime * result + ((weekDay == null) ? 0 : weekDay.hashCode());
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
		final OpeningDay other = (OpeningDay) obj;
		if (openingHours == null) {
			if (other.openingHours != null) {
				return false;
			}
		} else if (!openingHours.equals(other.openingHours)) {
			return false;
		}
		if (weekDay != other.weekDay) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OpeningDay [id=" + id + ", weekDay=" + weekDay + ", timetable=" + timetable + "]";
	}
}
