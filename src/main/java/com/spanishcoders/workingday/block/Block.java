package com.spanishcoders.workingday.block;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.spanishcoders.appointment.Appointment;
import com.spanishcoders.workingday.WorkingDay;

@Entity
public class Block implements Comparable<Block> {

	public final static Duration DEFAULT_BLOCK_LENGTH = Duration.of(30, ChronoUnit.MINUTES);

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	private LocalTime start;

	@NotNull
	private Duration length;

	@NotNull
	@ManyToOne
	private WorkingDay workingDay;

	@ManyToOne
	private Appointment appointment;

	public Block() {
		this.length = DEFAULT_BLOCK_LENGTH;
	}

	public Block(LocalTime start, WorkingDay workingDay) {
		this();
		this.start = start;
		this.workingDay = workingDay;
		workingDay.addBlock(this);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalTime getStart() {
		return start;
	}

	public void setStart(LocalTime start) {
		this.start = start;
	}

	public Duration getLength() {
		return length;
	}

	public void setLength(Duration length) {
		this.length = length;
	}

	public WorkingDay getWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(WorkingDay workingDay) {
		this.workingDay = workingDay;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	@Override
	public String toString() {
		return "Block{" + "id=" + id + ", start=" + start + ", length=" + length + ", workingDay=" + workingDay + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((workingDay == null) ? 0 : workingDay.hashCode());
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
		final Block other = (Block) obj;
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		if (workingDay == null) {
			if (other.workingDay != null) {
				return false;
			}
		} else if (!workingDay.equals(other.workingDay)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Block o) {
		if (start.equals(o.getStart())) {
			return workingDay.compareTo(o.getWorkingDay());
		} else {
			return start.compareTo(o.getStart());
		}
	}

	public boolean isContiguousTo(Block nextBlock) {
		boolean contiguous = false;
		if (nextBlock != null && nextBlock.getStart() != null) {
			final LocalTime nextBlockShouldStartAt = this.getStart().plus(this.getLength());
			contiguous = nextBlockShouldStartAt.equals(nextBlock.getStart());
		}
		return contiguous;
	}
}
