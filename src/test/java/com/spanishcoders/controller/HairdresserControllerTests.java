package com.spanishcoders.controller;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.model.WorkingDay;
import com.spanishcoders.services.HairdresserService;
import com.spanishcoders.services.WorkService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import static com.spanishcoders.TestDataFactory.mockAllWorks;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//This class does not use @WebMvcTest because it needs the DefaultHandlerMapping defined in WebMvcConfiguration
@SpringBootTest
public class HairdresserControllerTests extends PelukaapUnitTest {

    @MockBean
    private HairdresserService hairdresserService;

    @MockBean
    private WorkService workService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
        given(workService.get(any(Set.class))).willReturn(mockAllWorks());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getAvailableBlocksForDay() throws Exception {
        given(hairdresserService.getAvailableBlocksForDayByHairdresser(any(Set.class), any(LocalDate.class))).willReturn(Maps.newHashMap());
        LocalDate fiveDaysFromNow = LocalDate.now().plusDays(5);
        String isoDate = fiveDaysFromNow.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String requestUrl = "/hairdresser/blocks/free/" + isoDate + "/works=1;works=2";
        this.mockMvc.perform(get(requestUrl).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getScheduleForTheDayWithoutWorks() throws Exception {
        given(hairdresserService.getTodaysBlocksByHairdresser()).willReturn(Maps.newHashMap());
        this.mockMvc.perform(get("/hairdresser/schedule/today").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getScheduleForTheDayWithOneWork() throws Exception {
        given(hairdresserService.getTodaysBlocksByHairdresser()).willAnswer(invocation -> {
            Map<Hairdresser, Set<Block>> answer = Maps.newHashMap();
            Block block = mock(Block.class);
            given(block.getStart()).willReturn(LocalTime.now());
            given(block.getLength()).willReturn(Block.DEFAULT_BLOCK_LENGTH);
            given(block.getWorkingDay()).willReturn(mock(WorkingDay.class));
            Set<Block> blocks = Sets.newHashSet(block);
            Hairdresser hairdresser = mock(Hairdresser.class);
            given(hairdresser.getStatus()).willReturn(UserStatus.ACTIVE);
            answer.put(hairdresser, blocks);
            return answer;
        });
        this.mockMvc.perform(get("/hairdresser/schedule/today").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].availableBlocks.*", hasSize(1)));
    }
}