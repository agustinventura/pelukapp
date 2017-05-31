package com.spanishcoders.user.hairdresser;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.spanishcoders.user.UserStatus;

public interface HairdresserRepository extends CrudRepository<Hairdresser, Integer> {

	Hairdresser findByUsername(String username);

	Set<Hairdresser> findByStatus(UserStatus active);
}
