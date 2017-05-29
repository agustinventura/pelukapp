package com.spanishcoders.user.hairdresser;

import com.spanishcoders.user.UserStatus;

import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface HairdresserRepository extends CrudRepository<Hairdresser, Integer> {

    Set<Hairdresser> findByStatus(UserStatus active);
}

