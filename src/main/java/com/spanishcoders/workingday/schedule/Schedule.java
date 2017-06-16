package com.spanishcoders.workingday.schedule;

import java.time.LocalDate;
import java.util.Set;

import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.workingday.block.Block;

public class Schedule {

	private final Hairdresser hairdresser;

	private final LocalDate day;

	private final Set<Block> blocks;

	public Schedule(Hairdresser hairdresser, LocalDate day, Set<Block> blocks) {
		super();
		this.hairdresser = hairdresser;
		this.day = day;
		this.blocks = blocks;
	}

	public Hairdresser getHairdresser() {
		return hairdresser;
	}

	public LocalDate getDay() {
		return day;
	}

	public Set<Block> getBlocks() {
		return blocks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
		result = prime * result + ((day == null) ? 0 : day.hashCode());
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
		final Schedule other = (Schedule) obj;
		if (blocks == null) {
			if (other.blocks != null) {
				return false;
			}
		} else if (!blocks.equals(other.blocks)) {
			return false;
		}
		if (day == null) {
			if (other.day != null) {
				return false;
			}
		} else if (!day.equals(other.day)) {
			return false;
		}
		if (hairdresser == null) {
			if (other.hairdresser != null) {
				return false;
			}
		} else if (!hairdresser.equals(other.hairdresser)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Schedule [hairdresser=" + hairdresser + ", day=" + day + ", blocks=" + blocks + "]";
	}
}
