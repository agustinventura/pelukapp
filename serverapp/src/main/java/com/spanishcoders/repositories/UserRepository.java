package com.spanishcoders.repositories;

import com.spanishcoders.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pep on 12/05/2016.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}
