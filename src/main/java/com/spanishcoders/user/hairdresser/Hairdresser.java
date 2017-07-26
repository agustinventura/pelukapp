package com.spanishcoders.user.hairdresser;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;

@Entity
@DiscriminatorValue(value = Role.Values.WORKER)
public class Hairdresser extends AppUser {

	@OneToOne(mappedBy = "hairdresser", cascade = CascadeType.ALL)
	private Agenda agenda;

	public Hairdresser() {
		this.setRole(Role.WORKER);
	}

	public Hairdresser(String username, String password, String phone) {
		super(username, password, phone);
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	@Override
	public String toString() {
		return "Hairdresser [getUsername()=" + getUsername() + ", getStatus()=" + getStatus() + ", getName()="
				+ getName() + ", getPassword()=" + getPassword() + ", getId()=" + getId() + ", getPhone()=" + getPhone()
				+ ", getRole()=" + getRole() + ", getAppointments()=" + getAppointments() + ", toString()="
				+ super.toString() + ", hashCode()=" + hashCode() + ", getClass()=" + getClass() + "]";
	}
}
