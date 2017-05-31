package com.spanishcoders.user;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Created by pep on 04/08/2016.
 */
public class UserDTO {

	private Integer id;
	private String name;
	private String username;
	private String password;
	private String phone;
	private UserStatus status;
	private Set<Integer> appointments;

	public UserDTO() {
		appointments = Sets.newTreeSet();
	}

	public UserDTO(AppUser user) {
		this();
		this.setId(user.getId());
		this.setName(user.getName());
		this.setUsername(user.getUsername());
		// we don't set the password here
		this.setPhone(user.getPhone());
		this.setStatus(user.getStatus());
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

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus userStatus) {
		this.status = userStatus;
	}

	public Set<Integer> getAppointments() {
		return appointments;
	}

	public void setAppointments(Set<Integer> appointments) {
		this.appointments = appointments;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final UserDTO userDTO = (UserDTO) o;

		if (id != null ? !id.equals(userDTO.id) : userDTO.id != null) {
			return false;
		}
		return username != null ? username.equals(userDTO.username) : userDTO.username == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (username != null ? username.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "UserDTO{" + "id=" + id + ", name='" + name + '\'' + ", username='" + username + '\'' + ", password='"
				+ password + '\'' + ", phone='" + phone + '\'' + ", status=" + status + ", appointments=" + appointments
				+ '}';
	}
}
