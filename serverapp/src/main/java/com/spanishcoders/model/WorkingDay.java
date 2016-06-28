package com.spanishcoders.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull
    private LocalDate date;

    @NotNull
    @ManyToOne
    private Agenda agenda;

    @NotEmpty
    @OneToMany(mappedBy = "workingDay")
    private Set<Block> blocks;

    public WorkingDay() {
    }

    public WorkingDay(LocalDate date, Agenda agenda) {
        this.date = date;
        this.agenda = agenda;
    }

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

    @Override
    public String toString() {
        return "WorkingDay{" +
                "date=" + date +
                ", agenda=" + agenda +
                ", blocks=" + blocks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkingDay that = (WorkingDay) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return agenda != null ? agenda.equals(that.agenda) : that.agenda == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (agenda != null ? agenda.hashCode() : 0);
        return result;
    }
}
