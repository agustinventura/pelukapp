package com.spanishcoders.work;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

public interface WorkRepository extends CrudRepository<Work, Integer> {

	Set<Work> findByKind(WorkKind kind);
}
