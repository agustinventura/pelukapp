package com.spanishcoders.user;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by pep on 12/05/2016.
 */
public interface UserRepository extends CrudRepository<AppUser, Integer> {
    AppUser findByUsername(String username);
}
