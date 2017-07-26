package com.spanishcoders.workingday;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.SortedSet;

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

import com.google.common.collect.Sets;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.OpeningHours;
import com.spanishcoders.appointment.AppointmentStatus;
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
	private final SortedSet<Block> blocks;

	private WorkingDay() {
		this.blocks = Sets.newTreeSet();
	}

	public WorkingDay(LocalDate date) {
		this();
		this.date = date;
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

	@Override
	public int compareTo(WorkingDay o) {
		return date.compareTo(o.getDate());
	}

	public boolean hasValidAppointment() {
		return this.blocks.stream().anyMatch(block -> block.getAppointment() != null
				&& block.getAppointment().getStatus() == AppointmentStatus.VALID);
	}

	public void createBlocks(Set<OpeningHours> openingHours) {
		for (final OpeningHours openingHoursStretch : openingHours) {
			LocalTime startTime = openingHoursStretch.getStartTime();
			while (startTime.isBefore(openingHoursStretch.getEndTime())) {
				final Block newBlock = new Block(startTime);
				newBlock.setWorkingDay(this);
				this.blocks.add(newBlock);
				startTime = startTime.plusMinutes(Block.BLOCK_MINUTES);
			}
		}
	}
}
