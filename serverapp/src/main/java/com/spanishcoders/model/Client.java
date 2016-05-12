package com.spanishcoders.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */

@Data
@Entity
public class Client implements User {

    @Id
    private int id;

    @NotNull
    private String name;

    private String password;

    @NotNull
    private String phone;

    private String distance;

    @OneToMany(mappedBy = "client")
    @OrderBy(value = "startTime asc" )
    private Set<Appointment> appointments;


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
