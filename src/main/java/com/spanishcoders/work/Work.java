package com.spanishcoders.work;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "work")
public class Work implements Comparable<Work> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private Duration duration;

	@Enumerated(EnumType.STRING)
	private WorkKind kind;

	public Work() {
	}

	public Work(String name, Duration duration, WorkKind kind) {
		this.name = name;
		this.duration = duration;
		this.kind = kind;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Work work = (Work) o;

		if (id != null ? !id.equals(work.id) : work.id != null) {
			return false;
		}
		return name != null ? name.equals(work.name) : work.name == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(Work o) {
		return this.getId().compareTo(o.getId());
	}
}
