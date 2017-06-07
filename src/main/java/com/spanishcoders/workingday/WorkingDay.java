package com.spanishcoders.workingday;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.Stretch;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.workingday.block.Block;

@Entity
@Table(name = "working_day")
public class WorkingDay implements Comparable<WorkingDay> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	private LocalDate date;

	@NotNull
	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "working_day_agenda_fk"))
	private Agenda agenda;

	@OneToMany(mappedBy = "workingDay", cascade = CascadeType.ALL)
	@OrderBy("start asc")
	private SortedSet<Block> blocks;

	public WorkingDay() {
		this.blocks = new TreeSet<>();
	}

	public WorkingDay(Agenda agenda) {
		this();
		this.date = getNewWorkingDayDate(agenda.getNonWorkingDays(), agenda.getWorkingDays());
		final NavigableSet<Block> workingDayBlocks = createBlocksForDay(agenda.getCurrentTimetable());
		this.setBlocks(workingDayBlocks);
		this.agenda = agenda;
		agenda.addWorkingDay(this);
	}

	public WorkingDay(Agenda agenda, LocalDate date) {
		this();
		if (agenda.isNonWorkingDay(date)) {
			throw new IllegalArgumentException(
					"Can't create working day on agenda " + agenda + " non working day: " + date);
		}
		this.agenda = agenda;
		this.date = date;
		agenda.addWorkingDay(this);
		this.setBlocks(createBlocksForDay(agenda.getCurrentTimetable()));
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public SortedSet<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(SortedSet<Block> blocks) {
		this.blocks = blocks;
	}

	@Override
	public String toString() {
		return "WorkingDay{" + "id=" + id + ", date=" + date + ", agenda=" + agenda + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agenda == null) ? 0 : agenda.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
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
		final WorkingDay other = (WorkingDay) obj;
		if (agenda == null) {
			if (other.agenda != null) {
				return false;
			}
		} else if (!agenda.equals(other.agenda)) {
			return false;
		}
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		return true;
	}

	public void addBlock(Block block) {
		if (block == null) {
			throw new IllegalArgumentException("Can't add an empty block to working day");
		}
		this.blocks.add(block);
	}

	private LocalDate getNewWorkingDayDate(Set<LocalDate> nonWorkingDays,
			SortedMap<LocalDate, WorkingDay> workingDays) {
		LocalDate lastWorkingDayDate = null;
		if (workingDays.isEmpty()) {
			lastWorkingDayDate = LocalDate.now();
		} else {
			lastWorkingDayDate = workingDays.lastKey().plusDays(1);
		}
		if (nonWorkingDays != null && !nonWorkingDays.isEmpty()) {
			while (nonWorkingDays.contains(lastWorkingDayDate)) {
				lastWorkingDayDate = lastWorkingDayDate.plusDays(1);
			}
		}
		return lastWorkingDayDate;
	}

	private NavigableSet<Block> createBlocksForDay(Timetable timetable) {
		final NavigableSet<Block> newBlocks = new TreeSet<>();
		for (final Stretch stretch : timetable.getStretches()) {
			LocalTime startTime = stretch.getStartTime();
			while (startTime.isBefore(stretch.getEndTime())) {
				final Block newBlock = new Block(startTime, this);
				newBlocks.add(newBlock);
				startTime = startTime.plus(Block.DEFAULT_BLOCK_LENGTH);
			}
		}
		return newBlocks;
	}

	@Override
	public int compareTo(WorkingDay o) {
		return date.compareTo(o.getDate());
	}
}
