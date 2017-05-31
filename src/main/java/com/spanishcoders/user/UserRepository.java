package com.spanishcoders.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<AppUser, Integer> {
	AppUser findByUsername(String username);
}
