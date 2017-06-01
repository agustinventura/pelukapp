package com.spanishcoders.user.client;

import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Integer> {

	Client findByUsername(String username);
}
