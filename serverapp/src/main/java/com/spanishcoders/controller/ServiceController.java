package com.spanishcoders.controller;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Service;
import com.spanishcoders.repositories.ServiceRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by pep on 12/05/2016.
 */
@RestController
@RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {

    private ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Service> getServices() {
        return Sets.newHashSet(serviceRepository.findAll());
    }
}
