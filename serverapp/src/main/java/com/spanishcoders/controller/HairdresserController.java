package com.spanishcoders.controller;

import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.repositories.WorkRepository;
import com.spanishcoders.services.HairdresserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    private WorkRepository workRepository;

    public HairdresserController(HairdresserService hairdresserService, WorkRepository workRepository) {
        this.hairdresserService = hairdresserService;
        this.workRepository = workRepository;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "blocks/free", method = RequestMethod.GET)
    public Map<Hairdresser, Set<Block>> getWorks(Authentication authentication, @RequestParam(required = false) Integer work) {
        Work requestedWork = workRepository.findOne(work);
        return hairdresserService.getFirstTenAvailableBlocksByHairdresser(requestedWork);
    }
}
