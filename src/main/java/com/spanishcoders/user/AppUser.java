package com.spanishcoders.user;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Sets;
import com.spanishcoders.appointment.Appointment;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AppUser {

	@Id
	@GeneratedValue
	private Integer id;

	@NotNull
	private String name;

	@NotNull
	@Column(unique = true)
	private String username;

	@NotNull
	private String password;

	@NotNull
	private String phone;

	@NotNull
	private UserStatus status;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	@OrderBy(value = "date asc")
	private Set<Appointment> appointments;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public AppUser() {
		this.status = UserStatus.ACTIVE;
		this.appointments = Sets.newTreeSet();
	}

	public AppUser(String username, String password, String phone) {
		this();
		this.username = username;
		this.password = password;
		this.phone = phone;
	}

	public AppUser(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	public AppUser(UserDTO userDTO) {
		this();
		this.username = userDTO.getUsername();
		this.password = userDTO.getPassword();
		this.phone = userDTO.getPhone();
		this.name = userDTO.getName();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Set<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}

	@Override
	public String toString() {
		return "AppUser{" + "name='" + name + '\'' + ", username='" + username + '\'' + ", password='" + password + '\''
				+ ", phone='" + phone + '\'' + ", status=" + status + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final AppUser user = (AppUser) o;

		return username != null ? username.equals(user.username) : user.username == null;

	}

	@Override
	public int hashCode() {
		return username != null ? username.hashCode() : 0;
	}

	public void addAppointment(Appointment appointment) {
		this.appointments.add(appointment);
	}
}
