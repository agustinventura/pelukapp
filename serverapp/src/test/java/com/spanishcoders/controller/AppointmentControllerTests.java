package com.spanishcoders.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.AppointmentStatus;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.services.AppointmentService;
import com.spanishcoders.services.BlockService;
import com.spanishcoders.services.WorkService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppointmentController.class)
public class AppointmentControllerTests extends PelukaapUnitTest {

    public static final String APPOINTMENT_URL = "/appointment";
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
        answerAppointmentFromDTO();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(1);
        appointmentDTO.getBlocks().add(1);
        this.mockMvc.perform(post(APPOINTMENT_URL)
                .content(toJSON(appointmentDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$.blocks", hasSize(1)))
                .andExpect(jsonPath("$.works", hasSize(1)));
    }

    private String toJSON(AppointmentDTO appointmentDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(appointmentDTO);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getAppointmentWithTwoWorks() throws Exception {
        answerAppointmentFromDTO();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().addAll(Arrays.asList(1, 2));
        appointmentDTO.getBlocks().addAll(Arrays.asList(1, 2));
        this.mockMvc.perform(post(APPOINTMENT_URL)
                .content(toJSON(appointmentDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$.blocks", hasSize(2)))
                .andExpect(jsonPath("$.works", hasSize(2)));
    }

    private void answerAppointmentFromDTO() {
        given(appointmentService.confirmAppointment(any(Authentication.class), any(AppointmentDTO.class))).willAnswer(invocation -> {
            AppointmentDTO appointmentDTO = (AppointmentDTO) invocation.getArguments()[1];
            Appointment appointment = new Appointment();
            appointment.setBlocks(appointmentDTO.getBlocks().stream().map(blockId -> {
                Block block = new Block();
                block.setId(blockId);
                return block;
            }).collect(Collectors.toSet()));
            appointment.setWorks(appointmentDTO.getWorks().stream().map(workId -> {
                Work work = new Work();
                work.setId(workId);
                return work;
            }).collect(Collectors.toSet()));
            return appointment;
        });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getAppointmentWithInvalidPairingWorkBlock() throws Exception {
        given(appointmentService.confirmAppointment(any(Authentication.class), any(AppointmentDTO.class))).willThrow(new IllegalArgumentException());
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        this.mockMvc.perform(post(APPOINTMENT_URL)
                .content(toJSON(appointmentDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "client", roles = {"USER", "CLIENT"})
    public void getAppointmentWithPrivateWorkAsClient() throws Exception {
        given(appointmentService.confirmAppointment(any(Authentication.class), any(AppointmentDTO.class))).willThrow(new AccessDeniedException("Access denied"));
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        this.mockMvc.perform(post(APPOINTMENT_URL)
                .content(toJSON(appointmentDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void cancelInvalidAppointment() throws Exception {
        given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
            Optional<Appointment> appointment = Optional.empty();
            return appointment;
        });
        this.mockMvc.perform(put(APPOINTMENT_URL).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void cancelAppointmentWithMoreThan24Hours() throws Exception {
        given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
            Optional<Appointment> appointment = Optional.of(new Appointment());
            return appointment;
        });
        given(appointmentService.cancelAppointment(any(Authentication.class), any(Appointment.class))).willAnswer(invocation -> {
            Appointment appointment = new Appointment();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            return appointment;
        });
        this.mockMvc.perform(put(APPOINTMENT_URL).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void cancelAppointmentAsWorker() throws Exception {
        given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
            Optional<Appointment> appointment = Optional.of(new Appointment());
            return appointment;
        });
        given(appointmentService.cancelAppointment(any(Authentication.class), any(Appointment.class))).willAnswer(invocation -> {
            Appointment appointment = new Appointment();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            return appointment;
        });
        this.mockMvc.perform(put(APPOINTMENT_URL).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @WithMockUser(username = "client", roles = {"USER", "CLIENT"})
    public void cancelAppointmentWithLessThan24HoursAsClient() throws Exception {
        given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
            Optional<Appointment> appointment = Optional.of(new Appointment());
            return appointment;
        });
        given(appointmentService.cancelAppointment(any(Authentication.class), any(Appointment.class))).willThrow(AccessDeniedException.class);
        this.mockMvc.perform(put(APPOINTMENT_URL).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "client", roles = {"USER", "CLIENT"})
    public void cancelAppointmentWithMoreThan24HoursAsClient() throws Exception {
        given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
            Optional<Appointment> appointment = Optional.of(new Appointment());
            return appointment;
        });
        given(appointmentService.cancelAppointment(any(Authentication.class), any(Appointment.class))).willAnswer(invocation -> {
            Appointment appointment = new Appointment();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            return appointment;
        });
        this.mockMvc.perform(put(APPOINTMENT_URL).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }
}