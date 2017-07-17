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
	@CollectionTable(name = "closing_days", foreignKey = @ForeignKey(name = "closing_days_agenda_fk"))
	private Set<LocalDate> closingDays;

	@NotEmpty
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(foreignKey = @ForeignKey(name = "agenda_timetable_fk"))
	private Set<Timetable> timetables;

	public Agenda() {
		this.workingDays = new TreeMap<>();
		this.timetables = Sets.newHashSet();
		this.closingDays = Sets.newHashSet();
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

	public Set<LocalDate> getClosingDays() {
		return closingDays;
	}

	public void setClosingDays(Set<LocalDate> closingDays) {
		this.closingDays = closingDays;
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

	public void addClosingDay(LocalDate newClosingDay) {
		if (newClosingDay == null) {
			throw new IllegalArgumentException("To add a closing day it can't be null");
		}
		this.closingDays.add(newClosingDay);
	}

	public void addTimetable(Timetable timetable) {
		if (timetable == null) {
			throw new IllegalArgumentException("Can't add an empty timetable to agenda");
		}
		checkTimetablesOverlapping(timetable);
		this.timetables.add(timetable);
	}

	private void checkTimetablesOverlapping(Timetable newTimetable) {
		for (final Timetable timetable : timetables) {
			if (timetable.overlaps(newTimetable)) {
				throw new IllegalArgumentException(
						"New timetable " + newTimetable + " overlaps with timetable " + timetable);
			}
		}
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

	public boolean isClosingDay(LocalDate today) {
		return closingDays.contains(today);
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
