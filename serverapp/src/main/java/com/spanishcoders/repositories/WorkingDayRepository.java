package com.spanishcoders.repositories;

import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.WorkingDay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.SortedSet;

/**
 * Created by agustin on 6/07/16.
 */
public interface WorkingDayRepository extends CrudRepository<WorkingDay, Integer> {

    @Query("select wd from WorkingDay wd where wd.agenda = :agenda and wd.date >= CURRENT_DATE")
    SortedSet<WorkingDay> getNextWorkingDays(@Param("agenda") Agenda agenda);
}
