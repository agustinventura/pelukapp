package com.spanishcoders.repositories;

import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface HairdresserRepository extends CrudRepository<Hairdresser, Integer> {

    Set<Hairdresser> findByStatus(UserStatus active);
}

