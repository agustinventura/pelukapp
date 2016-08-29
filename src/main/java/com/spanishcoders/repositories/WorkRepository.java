package com.spanishcoders.repositories;

import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * Created by pep on 12/05/2016.
 */
public interface WorkRepository extends CrudRepository<Work, Integer> {

    Set<Work> findByKind(WorkKind kind);
}
