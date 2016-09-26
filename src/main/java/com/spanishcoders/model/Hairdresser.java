package com.spanishcoders.model;

import com.spanishcoders.model.dto.UserDTO;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by pep on 12/05/2016.
 */
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
    }

    @Override
    public String toString() {
        return "Hairdresser{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                '}';
    }
}
