package com.spanishcoders.repositories;

import com.spanishcoders.model.Service;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pep on 12/05/2016.
 */
public interface ServiceRepository extends CrudRepository<Service, Integer> {
}
