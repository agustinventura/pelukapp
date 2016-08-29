package com.spanishcoders.controller;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.User;
import com.spanishcoders.services.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests extends PelukaapUnitTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "client", roles = {"USER", "CLIENT"})
    public void getNextAppointments() throws Exception {
        given(userService.getNextAppointments(any(Authentication.class))).will(invocation -> {
            Authentication authentication = (Authentication) invocation.getArguments()[0];
            User mockUser = new User();
            mockUser.setUsername(authentication.getName());
            Appointment appointment = new Appointment();
            appointment.setUser(mockUser);
            return Sets.newHashSet(appointment);
        });
        this.mockMvc.perform(get("/user/appointments/next").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(1)));
    }
}