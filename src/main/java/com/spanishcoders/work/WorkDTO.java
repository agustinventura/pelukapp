package com.spanishcoders.work;

public class WorkDTO {

	private Integer id;

	private String name;

	private long duration;

	private WorkKind workKind;

	public WorkDTO() {
		super();
	}

	public WorkDTO(Work work) {
		super();
		this.id = work.getId();
		this.name = work.getName();
		this.duration = work.getDuration().toMinutes();
		this.workKind = work.getKind();
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

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public WorkKind getWorkKind() {
		return workKind;
	}

	public void setWorkKind(WorkKind workKind) {
		this.workKind = workKind;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((workKind == null) ? 0 : workKind.hashCode());
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
		if (duration != other.duration) {
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
		return true;
	}

}
