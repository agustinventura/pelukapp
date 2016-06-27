package com.spanishcoders.controller;

import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.services.HairdresserService;
import com.spanishcoders.services.WorkService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * Created by agustin on 21/06/16.
 */
@RestController
@RequestMapping(value = "/hairdresser", produces = MediaType.APPLICATION_JSON_VALUE)
public class HairdresserController {

    private HairdresserService hairdresserService;

    private WorkService workService;

    public HairdresserController(HairdresserService hairdresserService, WorkService workService) {
        this.hairdresserService = hairdresserService;
        this.workService = workService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "blocks/free/{works}", method = RequestMethod.GET)
    public Map<Hairdresser, Set<Block>> getWorks(Authentication authentication, @MatrixVariable Set<Integer> works) {
        Set<Work> requestedWorks = workService.get(works);
        return hairdresserService.getFirstTenAvailableBlocksByHairdresser(requestedWorks);
    }
}
