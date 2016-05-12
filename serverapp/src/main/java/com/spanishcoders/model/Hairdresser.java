package com.spanishcoders.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by pep on 12/05/2016.
 */

@Data
@Entity
public class Hairdresser implements User {

    @Id
    private int id;

    @NotNull
    private String name;

    private String password;

    @OneToMany(mappedBy = "hairdresser")
    @OrderBy("startTime asc")
    private List<Appointment> appointments;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
