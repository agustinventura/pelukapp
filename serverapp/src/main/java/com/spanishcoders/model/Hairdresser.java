package com.spanishcoders.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by pep on 12/05/2016.
 */
@Entity
public class Hairdresser extends User {

    @OneToOne(mappedBy = "hairdresser")
    private Agenda agenda;

    public Hairdresser() {
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
        return "Hairdresser{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                '}';
    }
}
