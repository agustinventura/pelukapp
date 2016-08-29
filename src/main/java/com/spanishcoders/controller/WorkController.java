package com.spanishcoders.controller;

import com.spanishcoders.model.Work;
import com.spanishcoders.services.WorkService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by pep on 12/05/2016.
 */
@RestController
@RequestMapping(value = "/works", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkController {

    private WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @PreAuthorize("authenticated")
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Work> getWorks(Authentication authentication) {
        return workService.getAvailableWorks(authentication.getAuthorities());
    }
}
