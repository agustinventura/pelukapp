package com.spanishcoders.user;

import java.util.Set;

import com.google.common.collect.Sets;
import com.spanishcoders.appointment.AppointmentDTO;

public class UserDTO {

	private Integer id;
	private String name;
	private String username;
	private String password;
	private String phone;
	private UserStatus status;
	private Role role;
	private final Set<AppointmentDTO> appointments = Sets.newTreeSet();

	public UserDTO() {
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus userStatus) {
		this.status = userStatus;
	}

	public Set<AppointmentDTO> getAppointments() {
		return appointments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		final UserDTO other = (UserDTO) obj;
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", name='" + name + '\'' + ", username='" + username + '\'' + ", password='"
				+ password + '\'' + ", phone='" + phone + '\'' + ", status=" + status + +'}';
	}
}
