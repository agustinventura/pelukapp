package com.spanishcoders.work;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "work")
public class Work implements Comparable<Work> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	private String name;

	@NotNull
	private Duration duration;

	@NotNull
	@Enumerated(EnumType.STRING)
	private WorkKind kind;

	@NotNull
	@Enumerated(EnumType.STRING)
	private WorkStatus status;

	public Work() {
		kind = WorkKind.PUBLIC;
		status = WorkStatus.ENABLED;
	}

	public Work(String name, Duration duration, WorkKind kind, WorkStatus status) {
		this.name = name;
		this.duration = duration;
		this.kind = kind;
		this.status = status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public WorkKind getKind() {
		return kind;
	}

	public void setKind(WorkKind kind) {
		this.kind = kind;
	}

	public WorkStatus getStatus() {
		return status;
	}

	public void setStatus(WorkStatus status) {
		this.status = status;
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
		final Work other = (Work) obj;
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		if (kind != other.kind) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (status != other.status) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Work [id=" + id + ", name=" + name + ", duration=" + duration + ", kind=" + kind + ", status=" + status
				+ "]";
	}

	@Override
	public int compareTo(Work o) {
		return this.getId().compareTo(o.getId());
	}
}
