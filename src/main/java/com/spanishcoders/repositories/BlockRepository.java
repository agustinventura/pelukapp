package com.spanishcoders.repositories;

import com.spanishcoders.model.Block;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by agustin on 7/07/16.
 */
public interface BlockRepository extends CrudRepository<Block, Integer> {
}
