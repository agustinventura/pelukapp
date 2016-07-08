package com.spanishcoders.controller;

import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.User;
import com.spanishcoders.model.Work;
import com.spanishcoders.services.AppointmentService;
import com.spanishcoders.services.BlockService;
import com.spanishcoders.services.WorkService;
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

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//This class does not use @WebMvcTest because it needs the DefaultHandlerMapping defined in WebMvcConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppointmentControllerTests {

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private WorkService workService;

    @MockBean
    private BlockService blockService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
        given(workService.get(any(Set.class))).willAnswer(invocation -> {
            Set<Integer> workIds = (Set<Integer>) invocation.getArguments()[0];
            Set<Work> works = workIds.stream().map(id -> {
                Work work = new Work();
                work.setId(id);
                return work;
            }).collect(Collectors.toSet());
            return works;
        });
        given(blockService.get(any(Set.class))).willAnswer(invocation -> {
            Set<Integer> blockIds = (Set<Integer>) invocation.getArguments()[0];
            Set<Block> blocks = blockIds.stream().map(id -> {
                Block block = new Block();
                block.setId(id);
                return block;
            }).collect(Collectors.toSet());
            return blocks;
        });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getAppointmentWithOneWork() throws Exception {
        given(appointmentService.confirmAppointment(any(User.class), any(Set.class), any(Set.class))).willAnswer(invocation -> {
            Set<Work> works = (Set<Work>) invocation.getArguments()[0];
            Set<Block> blocks = (Set<Block>) invocation.getArguments()[1];
            Appointment appointment = new Appointment();
            appointment.setBlocks(blocks);
            appointment.setWorks(works);
            return appointment;
        });
        this.mockMvc.perform(get("/appointment/new/works=1&blocks=1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.blocks", hasSize(1)))
                .andExpect(jsonPath("$.works", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getAppointmentWithTwoWorks() throws Exception {
        given(appointmentService.confirmAppointment(any(User.class), any(Set.class), any(Set.class))).willAnswer(invocation -> {
            Set<Work> works = (Set<Work>) invocation.getArguments()[0];
            Set<Block> blocks = (Set<Block>) invocation.getArguments()[1];
            Appointment appointment = new Appointment();
            appointment.setBlocks(blocks);
            appointment.setWorks(works);
            return appointment;
        });
        this.mockMvc.perform(get("/appointment/new/works=1;works=2&blocks=1;blocks=2").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.blocks", hasSize(2)))
                .andExpect(jsonPath("$.works", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getAppointmentWithInvalidPairingWorkBlock() throws Exception {
        given(appointmentService.confirmAppointment(any(User.class), any(Set.class), any(Set.class))).willThrow(new IllegalStateException());
        this.mockMvc.perform(get("/appointment/new/works=1&blocks=1;blocks=2").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isBadRequest());
    }
}