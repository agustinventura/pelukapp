package com.spanishcoders.agenda;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Map;
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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

	@OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKey(name = "date")
	@OrderBy("date")
	private final SortedMap<LocalDate, WorkingDay> workingDays;

	@ElementCollection
	@CollectionTable(name = "closing_days", foreignKey = @ForeignKey(name = "closing_days_agenda_fk"))
	private final Set<LocalDate> closingDays;

	@NotEmpty
	@OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL)
	private final Set<Timetable> timetables;

	private Agenda() {
		this.workingDays = new TreeMap<>();
		this.timetables = Sets.newHashSet();
		this.closingDays = Sets.newHashSet();
	}

	public Agenda(Hairdresser hairdresser) {
		this();
		this.hairdresser = hairdresser;
		hairdresser.setAgenda(this);
	}

	public Integer getId() {
		return id;
	}

	public Hairdresser getHairdresser() {
		return hairdresser;
	}

	public Map<LocalDate, WorkingDay> getWorkingDays() {
		return ImmutableMap.copyOf(workingDays);
	}

	public Set<LocalDate> getClosingDays() {
		return ImmutableSet.copyOf(closingDays);
	}

	public Set<Timetable> getTimetables() {
		return ImmutableSet.copyOf(timetables);
	}

	@Override
	public String toString() {
		return "Agenda [id=" + id + ", hairdresser=" + hairdresser + "]";
	}

	public void addWorkingDay(LocalDate day) {
		if (day == null) {
			throw new IllegalArgumentException("To add a working day it needs to have a date");
		}
		if (!isClosingDay(day) && isInAnyTimetable(day)) {
			createWorkingDay(day);
		}
	}

	private void createWorkingDay(LocalDate day) {
		final WorkingDay workingDay = new WorkingDay(day);
		workingDay.setAgenda(this);
		workingDays.put(workingDay.getDate(), workingDay);
		createBlocks(workingDay);
	}

	private void createBlocks(WorkingDay workingDay) {
		final Timetable applicableTimetable = this.timetables.stream()
				.filter(timetable -> timetable.contains(workingDay.getDate())).findFirst().get();
		workingDay.createBlocks(applicableTimetable.getOpeningHoursForDay(workingDay.getDate()));
	}

	private boolean isInAnyTimetable(LocalDate day) {
		return this.timetables.stream().filter(timetable -> timetable.contains(day)).findFirst()
				.map(timetable -> timetable.getOpeningDays().stream()
						.anyMatch(openingDay -> openingDay.getWeekDay() == day.getDayOfWeek()))
				.orElse(false);
	}

	public void addClosingDay(LocalDate newClosingDay) {
		if (newClosingDay == null) {
			throw new IllegalArgumentException("To add a closing day it can't be null");
		}
		checkCurrentAppointments(newClosingDay);
		this.closingDays.add(newClosingDay);
	}

	private void checkCurrentAppointments(LocalDate newClosingDay) {
		if (this.workingDays.containsKey(newClosingDay)) {
			if (this.workingDays.get(newClosingDay).hasValidAppointment()) {
				throw new IllegalStateException("New closing day " + newClosingDay + " already has appointments");
			}
		}
	}

	public void addTimetable(Timetable timetable) {
		if (timetable == null) {
			throw new IllegalArgumentException("Can't add an empty timetable to agenda");
		}
		checkTimetablesOverlapping(timetable);
		this.timetables.add(timetable);
		timetable.setAgenda(this);
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
			if (currentTimetable.contains(today)) {
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
		final Set<Block> blocks = Sets.newHashSet();
		if (workingDays.containsKey(day)) {
			blocks.addAll(workingDays.get(day).getBlocks());
		}
		return ImmutableSet.copyOf(blocks);
	}

	public void modifyStartDate(LocalDate newStartDate, Timetable timetable) {
		checkStartDate(newStartDate, timetable);
		if (this.timetables.stream().anyMatch(tt -> tt.contains(newStartDate))) {
			modifyStartDateWithOverlap(newStartDate, timetable);
		} else {
			modifyStartDateWithoutOverlap(newStartDate, timetable);
		}
	}

	public void modifyEndDate(LocalDate newEndDate, Timetable timetable) {
		checkStartDate(newEndDate, timetable);
		if (this.timetables.stream().anyMatch(tt -> tt.contains(newEndDate))) {
			modifyEndDateWithOverlap(newEndDate, timetable);
		} else {
			modifyEndDateWithoutOverlap(newEndDate, timetable);
		}
	}

	private void modifyEndDateWithoutOverlap(LocalDate newEndDate, Timetable timetable) {
		timetable.setEndDate(newEndDate);
	}

	private void modifyEndDateWithOverlap(LocalDate newEndDate, Timetable timetable) {
		final Timetable overlappedTimetable = this.timetables.stream().filter(tt -> tt.contains(newEndDate)).findFirst()
				.get();
		if (!overlappedTimetable.equals(timetable)) {
			final LocalDate oldStartDate = overlappedTimetable.getStartDate();
			timetable.setEndDate(newEndDate);
			overlappedTimetable.setStartDate(newEndDate.plusDays(1));
			checkConflictedAppointments(timetable, oldStartDate, newEndDate);
			recalculateWorkingDaysBlocks(oldStartDate, newEndDate, timetable);
		} else {
			final LocalDate oldEndDate = timetable.getEndDate();
			timetable.setEndDate(newEndDate);
			checkOrphanAppointments(timetable, newEndDate, oldEndDate);
			removeWorkingDays(newEndDate, oldEndDate);
		}
	}

	private void modifyStartDateWithOverlap(LocalDate newStartDate, Timetable timetable) {
		final Timetable overlappedTimetable = this.timetables.stream().filter(tt -> tt.contains(newStartDate))
				.findFirst().get();
		if (!overlappedTimetable.equals(timetable)) {
			final LocalDate oldEndDate = overlappedTimetable.getEndDate();
			timetable.setStartDate(newStartDate);
			overlappedTimetable.setEndDate(newStartDate.minusDays(1));
			checkConflictedAppointments(timetable, newStartDate, oldEndDate);
			recalculateWorkingDaysBlocks(newStartDate, oldEndDate, timetable);
		} else {
			final LocalDate oldStartDate = timetable.getStartDate();
			timetable.setStartDate(newStartDate);
			checkOrphanAppointments(timetable, oldStartDate, newStartDate);
			removeWorkingDays(oldStartDate, newStartDate);
		}
	}

	private void removeWorkingDays(LocalDate oldStartDate, LocalDate newStartDate) {
		LocalDate day = oldStartDate;
		while (!day.equals(newStartDate)) {
			if (this.workingDays.containsKey(day)) {
				this.workingDays.remove(day);
			}
			day = day.plusDays(1);
		}
	}

	private void checkOrphanAppointments(Timetable timetable, LocalDate oldStartDate, LocalDate newStartDate) {
		LocalDate day = oldStartDate;
		while (day.isBefore(newStartDate) || day.isEqual(newStartDate)) {
			if (this.hasWorkingDay(day) && this.workingDays.get(day).hasValidAppointment()) {
				throw new IllegalStateException("Start date modification invalidates valid appointments");
			}
			day = day.plusDays(1);
		}
	}

	private void checkConflictedAppointments(Timetable timetable, LocalDate startDateCheck, LocalDate endDateCheck) {
		LocalDate day = startDateCheck;
		while (day.isBefore(endDateCheck) || day.isEqual(endDateCheck)) {
			if (this.hasWorkingDay(day) && this.workingDays.get(day).hasValidAppointment()) {
				if (!appointmentStillValid(this.getWorkingDayBlocks(day), timetable)) {
					throw new IllegalStateException("Start date modification invalidates valid appointments");
				}
			}
			day = day.plusDays(1);
		}
	}

	private boolean appointmentStillValid(Set<Block> dayBlocks, Timetable timetable) {
		return dayBlocks.stream().filter(block -> block.getAppointment() != null
				&& timetable.contains(block.getAppointment().getDate().toLocalDate())).map(block -> {
					final LocalTime appointmentTime = block.getAppointment().getDate().toLocalTime();
					return timetable.getOpeningHoursForDay(block.getAppointment().getDate().toLocalDate()).stream()
							.anyMatch(openingHours -> openingHours.contains(appointmentTime));
				}).findFirst().orElse(true);
	}

	private void modifyStartDateWithoutOverlap(LocalDate newStartDate, Timetable timetable) {
		timetable.setStartDate(newStartDate);
	}

	private void recalculateWorkingDaysBlocks(LocalDate newStartDate, LocalDate oldStartDate, Timetable timetable) {
		LocalDate day = newStartDate;
		while (!day.equals(oldStartDate)) {
			workingDayStillValid(timetable, day);
			day = day.plusDays(1);
		}

	}

	private void workingDayStillValid(Timetable timetable, LocalDate day) {
		if (this.hasWorkingDay(day)) {
			if (timetable.contains(day)) {
				final Set<Block> oldBlocks = this.workingDays.get(day).getBlocks();
				this.workingDays.remove(day);
				this.addWorkingDay(day);
				for (final Block oldBlock : oldBlocks) {
					if (this.workingDays.get(day).getBlocks().contains(oldBlock)) {
						for (final Block newBlock : workingDays.get(day).getBlocks()) {
							if (newBlock.equals(oldBlock)) {
								newBlock.setAppointment(oldBlock.getAppointment());
							}
						}
					}
				}
			} else {
				this.workingDays.remove(day);
			}
		}
	}

	private void checkStartDate(LocalDate newStartDate, Timetable timetable) {
		if (newStartDate == null) {
			throw new IllegalArgumentException("Can't use an empty start date on a timetable");
		}
		if (!this.timetables.contains(timetable)) {
			throw new IllegalArgumentException("Agenda doesn't contains the specified timetable");
		}
		if (timetable.getStartDate().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Can't modify a timetable start date in the past");
		}
	}

	private void checkEndDate(LocalDate newEndDate, Timetable timetable) {
		if (newEndDate == null) {
			throw new IllegalArgumentException("Can't use an empty end date on a timetable");
		}
		if (!this.timetables.contains(timetable)) {
			throw new IllegalArgumentException("Agenda doesn't contains the specified timetable");
		}
		if (timetable.getEndDate().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Can't modify a timetable end date in the past");
		}
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
}
