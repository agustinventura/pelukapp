package com.spanishcoders.controller;

import com.spanishcoders.model.Service;
import com.spanishcoders.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pep on 12/05/2016.
 */
@RestController
@RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {

    private ServiceRepository serviceRepository;

    @Autowired
    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Service> getServices() {
        List<Service> services = new ArrayList<>();
        serviceRepository.findAll().forEach(services::add);

        return services;
    }
}
