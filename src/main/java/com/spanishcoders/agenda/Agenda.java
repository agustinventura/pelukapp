package com.spanishcoders.agenda;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.collect.Sets;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

@Entity
@Table(name = "agenda")
public class Agenda {

	@Id
	private Integer id;

	@NotNull
	@OneToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "agenda_hairdresser_fk"))
	@MapsId
	private Hairdresser hairdresser;

	@OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL)
	@MapKey(name = "date")
	@OrderBy("date")
	private SortedMap<LocalDate, WorkingDay> workingDays;

	@ElementCollection
	@CollectionTable(name = "non_working_days", foreignKey = @ForeignKey(name = "non_working_days_agenda_fk"))
	private Set<LocalDate> nonWorkingDays;

	@NotEmpty
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(foreignKey = @ForeignKey(name = "agenda_timetable_fk"))
	private Set<Timetable> timetables;

	public Agenda() {
		this.workingDays = new TreeMap<>();
		this.timetables = Sets.newHashSet();
		this.nonWorkingDays = Sets.newHashSet();
	}

	public Agenda(Hairdresser hairdresser, Timetable... timetables) {
		this();
		hairdresser.setAgenda(this);
		this.timetables.addAll(Arrays.asList(timetables));
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Hairdresser getHairdresser() {
		return hairdresser;
	}

	public void setHairdresser(Hairdresser hairdresser) {
		this.hairdresser = hairdresser;
	}

	public SortedMap<LocalDate, WorkingDay> getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(SortedMap<LocalDate, WorkingDay> workingDays) {
		this.workingDays = workingDays;
	}

	public Set<LocalDate> getNonWorkingDays() {
		return nonWorkingDays;
	}

	public void setNonWorkingDays(Set<LocalDate> nonWorkingDays) {
		this.nonWorkingDays = nonWorkingDays;
	}

	public Set<Timetable> getTimetables() {
		return timetables;
	}

	public void setTimetables(Set<Timetable> timetables) {
		this.timetables = timetables;
	}

	@Override
	public String toString() {
		return "Agenda{" + "id=" + id + ", hairdresser=" + hairdresser + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hairdresser == null) ? 0 : hairdresser.hashCode());
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
		final Agenda other = (Agenda) obj;
		if (hairdresser == null) {
			if (other.hairdresser != null) {
				return false;
			}
		} else if (!hairdresser.equals(other.hairdresser)) {
			return false;
		}
		return true;
	}

	public void addWorkingDay(WorkingDay workingDay) {
		if (workingDay == null || workingDay.getDate() == null) {
			throw new IllegalArgumentException("To add a working day it needs to have a date");
		}
		workingDays.put(workingDay.getDate(), workingDay);
	}

	public void addTimetable(Timetable timetable) {
		if (timetable == null) {
			throw new IllegalArgumentException("Can't add an empty timetable to agenda");
		}
		this.timetables.add(timetable);
	}

	public Timetable getCurrentTimetable() {
		boolean found = false;
		final LocalDate today = LocalDate.now();
		Timetable currentTimetable = null;
		final Iterator<Timetable> timetablesIt = timetables.iterator();
		while (!found && timetablesIt.hasNext()) {
			currentTimetable = timetablesIt.next();
			if (currentTimetable.getStartDay().isBefore(today) && currentTimetable.getEndDay().isAfter(today)) {
				found = true;
			}
		}
		if (!found) {
			throw new TimeTableNotFoundException("Couldn't find an active timetable for date " + today);
		}
		return currentTimetable;
	}

	public void addNonWorkingDay(LocalDate nonWorkingDay) {
		this.nonWorkingDays.add(nonWorkingDay);
	}

	public boolean isNonWorkingDay(LocalDate today) {
		return nonWorkingDays.contains(today);
	}

	public boolean hasWorkingDay(LocalDate today) {
		return workingDays.containsKey(today);
	}

	public Set<Block> getWorkingDayBlocks(LocalDate day) {
		Set<Block> blocks = Sets.newHashSet();
		if (workingDays.containsKey(day)) {
			blocks = workingDays.get(day).getBlocks();
		}
		return blocks;
	}
}
