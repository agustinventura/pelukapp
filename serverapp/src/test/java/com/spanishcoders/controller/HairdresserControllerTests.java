package com.spanishcoders.controller;

import com.spanishcoders.repositories.WorkRepository;
import com.spanishcoders.services.HairdresserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.mockAllWorks;
import static com.spanishcoders.TestDataFactory.mockBlocksByHairdresser;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//This class does not use @WebMvcTest because it need the DefaultHandlerMapping defined in WebMvcConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class HairdresserControllerTests {

    @MockBean
    private HairdresserService hairdresserService;

    @MockBean
    private WorkRepository workRepository;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        given(workRepository.findAll(any(Collection.class))).willReturn(mockAllWorks());
        given(hairdresserService.getFirstTenAvailableBlocksByHairdresser(any(Set.class))).willReturn(mockBlocksByHairdresser());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getFirstTenAvailableBlocksWithOneWork() throws Exception {
        this.mockMvc.perform(get("/hairdresser/blocks/free/works=1;works=2").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*.*", hasSize(10)));
    }
}