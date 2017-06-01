package com.spanishcoders.appointment;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.workingday.block.Block;

@Entity
public class Appointment implements Comparable<Appointment> {

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	@OneToMany(mappedBy = "appointment", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH }, fetch = FetchType.EAGER)
	private Set<Block> blocks;

	@NotEmpty
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Work> works;

	@NotNull
	@ManyToOne
	private AppUser user;

	@NotNull
	private LocalDateTime date;

	@NotNull
	private Duration duration;

	@NotNull
	private AppointmentStatus status;

	private String notes;

	public Appointment() {
		blocks = Sets.newTreeSet();
		works = Sets.newTreeSet();
		status = AppointmentStatus.VALID;
	}

	public Appointment(AppUser user, Set<Work> works, Set<Block> blocks, String notes) {
		this();
		checkAuthentication(user);
		checkWorks(works);
		checkBlocks(blocks);
		checkAuthorization(user, works);
		checkWorkLength(works, blocks);
		this.user = user;
		user.addAppointment(this);
		this.works = works;
		this.blocks.addAll(blocks);
		this.blocks.stream().forEach(block -> block.setAppointment(this));
		final Block firstBlock = ((SortedSet<Block>) this.blocks).first();
		final LocalTime appointmentTime = firstBlock.getStart();
		final LocalDate appointmentDate = firstBlock.getWorkingDay().getDate();
		this.date = LocalDateTime.of(appointmentDate, appointmentTime);
		this.duration = Duration.of(works.stream().mapToLong(work -> work.getDuration().toMinutes()).sum(),
				ChronoUnit.MINUTES);
		this.notes = notes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Block> blocks) {
		this.blocks = blocks;
	}

	public Set<Work> getWorks() {
		return works;
	}

	public void setWorks(Set<Work> works) {
		this.works = works;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public Duration getDuration() {
		return duration;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((works == null) ? 0 : works.hashCode());
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
		final Appointment other = (Appointment) obj;
		if (blocks == null) {
			if (other.blocks != null) {
				return false;
			}
		} else if (!blocks.equals(other.blocks)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		if (works == null) {
			if (other.works != null) {
				return false;
			}
		} else if (!works.equals(other.works)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Appointment o) {
		return this.date.compareTo(o.getDate());
	}

	@Override
	public String toString() {
		return "Appointment{" + "id=" + id + ", user=" + user.getUsername() + ", duration=" + duration + ", date="
				+ date + ", blocks=" + blocks.stream().mapToInt(block -> block.getId()).toArray() + ", works="
				+ works.stream().mapToInt(work -> work.getId()).toArray() + ", notes=" + notes + '}';
	}

	private void checkWorkLength(Set<Work> requestedWorks, Set<Block> requestedBlocks) {
		final long worksLength = requestedWorks.stream().mapToLong(work -> work.getDuration().toMinutes()).sum();
		final long blocksLength = requestedBlocks.stream().mapToLong(block -> block.getLength().toMinutes()).sum();
		if (notEnoughBlocks(worksLength, blocksLength) && !tooManyblocks(worksLength, blocksLength)) {
			throw new IllegalArgumentException("Can't create an Appointment without enough Blocks for the Works");
		}
	}

	private boolean tooManyblocks(long worksLength, long blocksLength) {
		return (blocksLength - worksLength) > Block.DEFAULT_BLOCK_LENGTH.toMinutes();
	}

	private boolean notEnoughBlocks(long worksLength, long blocksLength) {
		return blocksLength < worksLength;
	}

	private void checkAuthorization(AppUser user, Set<Work> requestedWorks) {
		if (requestedWorks.stream().anyMatch(work -> work.getKind() == WorkKind.PRIVATE)) {
			if (Role.getRole(user) != Role.WORKER) {
				throw new AccessDeniedException("A Client can't create an Appointment with private Works");
			}
		}
	}

	private void checkBlocks(Set<Block> requestedBlocks) {
		if (CollectionUtils.isEmpty(requestedBlocks)) {
			throw new IllegalArgumentException("Can't create an Appointment without Blocks");
		}
		if (requestedBlocks.stream().anyMatch(block -> block.getAppointment() != null)) {
			throw new IllegalStateException("Can't create an Appointment with Blocks with Appointments");
		}
		if (!blocksAreContiguous(requestedBlocks)) {
			throw new IllegalArgumentException("Can't create an Appointmnet with separated Blocks");
		}
	}

	private boolean blocksAreContiguous(Set<Block> requestedBlocks) {
		boolean contiguous = true;
		final Iterator<Block> blockIt = requestedBlocks.iterator();
		while (contiguous && blockIt.hasNext()) {
			final Block block = blockIt.next();
			if (blockIt.hasNext()) {
				final Block nextBlock = blockIt.next();
				if (!block.isContiguousTo(nextBlock)) {
					contiguous = false;
				} else {
					final NavigableSet<Block> remainingBlocks = Sets.newTreeSet(requestedBlocks).tailSet(block, false);
					contiguous = blocksAreContiguous(remainingBlocks);
				}
			}
		}
		return contiguous;
	}

	private void checkWorks(Set<Work> requestedWorks) {
		if (CollectionUtils.isEmpty(requestedWorks)) {
			throw new IllegalArgumentException("Can't create an Appointment without Works");
		}
	}

	private void checkAuthentication(AppUser user) {
		if (user == null) {
			throw new AccessDeniedException("Can't create an Appointment without AppUser");
		}
	}

	public void cancel() {
		blocks.stream().forEach(block -> block.setAppointment(null));
		blocks.clear();
		status = AppointmentStatus.CANCELLED;
	}
}
