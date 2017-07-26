package com.spanishcoders.agenda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

@Entity
@Table(name = "timetable")
public class Timetable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	@NotEmpty
	@OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL)
	private final Set<OpeningDay> openingDays;

	@NotNull
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "agenda_timetable_fk"))
	private Agenda agenda;

	private Timetable() {
		this.startDate = null;
		this.endDate = null;
		this.openingDays = Sets.newTreeSet();
	}

	public Timetable(LocalDate startDate, LocalDate endDate) {
		this();
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Can't create a Timetable with start date after end date");
		}
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public Set<OpeningDay> getOpeningDays() {
		return ImmutableSet.copyOf(openingDays);
	}

	public void addOpeningDay(DayOfWeek weekDay, OpeningHours... openingHours) {
		if (weekDay == null) {
			throw new IllegalArgumentException("Can't add an opening day without week day");
		}
		if (openingHours == null || openingHours.length == 0) {
			throw new IllegalArgumentException("Can't add an opening day without opening hours");
		}
		if (this.openingDays.stream().anyMatch(openingDay -> openingDay.getWeekDay().equals(weekDay))) {
			throw new IllegalArgumentException("Can't add an opening day for existing day of week: " + weekDay);
		}
		final OpeningDay openingDay = new OpeningDay(this, weekDay, openingHours);
		this.openingDays.add(openingDay);
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public boolean overlaps(Timetable timetable) {
		boolean overlaps = false;
		if (timetable != null) {
			final Range<LocalDate> dateRange = Range.closed(startDate, endDate);
			overlaps = (dateRange.contains(timetable.getStartDate()) || dateRange.contains(timetable.getEndDate()));
		}
		return overlaps;
	}

	public boolean contains(LocalDate day) {
		boolean contains = false;
		if (day != null) {
			final Range<LocalDate> dateRange = Range.closed(startDate, endDate);
			contains = dateRange.contains(day);
		}
		return contains;
	}

	public Set<OpeningHours> getOpeningHoursForDay(LocalDate date) {
		final Set<OpeningHours> openingHours = Sets.newHashSet();
		if (date != null && contains(date)) {
			final DayOfWeek weekDay = date.getDayOfWeek();
			openingHours.addAll(openingDays.stream().filter(openingDay -> openingDay.getWeekDay().equals(weekDay))
					.flatMap(openingDay -> openingDay.getOpeningHours().stream()).collect(Collectors.toSet()));
		}
		return openingHours;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((openingDays == null) ? 0 : openingDays.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (openingDays == null) {
			if (other.openingDays != null) {
				return false;
			}
		} else if (!openingDays.equals(other.openingDays)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Timetable [id=" + id + ", startDate=" + startDate + ", endDate=" + endDate + ", agenda=" + agenda + "]";
	}

}
