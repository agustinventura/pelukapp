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

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }
}
