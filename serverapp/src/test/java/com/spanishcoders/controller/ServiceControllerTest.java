package com.spanishcoders.controller;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Service;
import com.spanishcoders.repositories.ServiceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ServiceController.class)
public class ServiceControllerTest {

    @MockBean
    private ServiceRepository serviceRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        Service cut = new Service("Corte", 30);
        Service shave = new Service("Afeitado", 30);
        given(serviceRepository.findAll()).willReturn(Sets.newHashSet(cut, shave));
    }

    @Test
    public void getServices() throws Exception {
        this.mockMvc.perform(get("/services").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
}