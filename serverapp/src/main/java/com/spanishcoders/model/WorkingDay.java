package com.spanishcoders.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by agustin on 16/06/16.
 */
@Entity
public class WorkingDay {

    @Id
    @GeneratedValue
    protected Integer id;

    private LocalDate date;

    @ManyToOne
    private Agenda agenda;

    @OneToMany(mappedBy = "workingDay")
    private Set<Block> blocks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }
}
