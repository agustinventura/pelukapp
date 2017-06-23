package com.spanishcoders.work;

import java.time.Duration;

public class WorkDTO {

	private Integer id;

	private String name;

	private Duration duration;

	private WorkKind workKind;

	private WorkStatus workStatus;

	public WorkDTO() {
		super();
	}

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

	public WorkKind getWorkKind() {
		return workKind;
	}

	public void setWorkKind(WorkKind workKind) {
		this.workKind = workKind;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((workKind == null) ? 0 : workKind.hashCode());
		result = prime * result + ((workStatus == null) ? 0 : workStatus.hashCode());
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
		final WorkDTO other = (WorkDTO) obj;
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (workKind != other.workKind) {
			return false;
		}
		if (workStatus != other.workStatus) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "WorkDTO [id=" + id + ", name=" + name + ", duration=" + duration + ", workKind=" + workKind
				+ ", workStatus=" + workStatus + "]";
	}
}
