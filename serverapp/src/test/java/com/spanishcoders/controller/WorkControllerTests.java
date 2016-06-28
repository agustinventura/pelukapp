package com.spanishcoders.controller;

import com.spanishcoders.services.WorkService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;

import static com.spanishcoders.TestDataFactory.mockAllWorks;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = WorkController.class, secure = true)
public class WorkControllerTests {

    @MockBean
    private WorkService workService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        given(workService.getAvailableWorks(any(Collection.class))).willReturn(mockAllWorks());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getWorksAdmin() throws Exception {
        this.mockMvc.perform(get("/works").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(3)));
    }
}