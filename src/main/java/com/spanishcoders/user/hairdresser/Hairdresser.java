package com.spanishcoders.user.hairdresser;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserDTO;

@Entity
public class Hairdresser extends AppUser {

	@OneToOne(mappedBy = "hairdresser", cascade = CascadeType.ALL)
	private Agenda agenda;

	public Hairdresser() {
	}

	public Hairdresser(String username, String password, String phone) {
		super(username, password, phone);
	}

	public Hairdresser(UserDTO userDTO) {
		super(userDTO);
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
		agenda.setHairdresser(this);
	}

	@Override
	public String toString() {
		return "Hairdresser [agenda=" + agenda + ", getUsername()=" + getUsername() + ", getStatus()=" + getStatus()
				+ ", getName()=" + getName() + ", getPassword()=" + getPassword() + ", getId()=" + getId()
				+ ", getPhone()=" + getPhone() + ", getAppointments()=" + getAppointments() + "]";
	}
}
