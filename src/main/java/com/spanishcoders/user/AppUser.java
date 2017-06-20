package com.spanishcoders.user;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Sets;
import com.spanishcoders.appointment.Appointment;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }, name = "username_uk"))
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	private String name;

	@NotNull
	@Column
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

	@NotNull
	@Column(name = "role", nullable = false, insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

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
	
	public void setId(Integer id) {
		this.id = id;
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
		appointment.setUser(this);
		this.appointments.add(appointment);
	}
}
