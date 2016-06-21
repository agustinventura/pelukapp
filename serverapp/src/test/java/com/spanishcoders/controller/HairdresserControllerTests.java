package com.spanishcoders.controller;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.WorkKind;
import com.spanishcoders.repositories.WorkRepository;
import com.spanishcoders.services.HairdresserService;
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

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static com.spanishcoders.model.Block.DEFAULT_BLOCK_LENGTH;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HairdresserController.class, secure = true)
public class HairdresserControllerTests {

    @MockBean
    private HairdresserService hairdresserService;

    @MockBean
    private WorkRepository workRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        given(workRepository.findOne(any(Integer.class))).willReturn(cut);
        LocalTime startTime = LocalTime.of(9, 00);
        Set<Block> testBlocks = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            startTime = startTime.plus(DEFAULT_BLOCK_LENGTH);
            testBlocks.add(new Block(startTime, null));
        }
        Map<Hairdresser, Set<Block>> availableBlocksByHairDresser = Maps.newHashMap();
        Hairdresser hairdresser = new Hairdresser("admin", "admin", "phone");
        availableBlocksByHairDresser.put(hairdresser, testBlocks);
        given(hairdresserService.getFirstTenAvailableBlocksByHairdresser(any(Work.class))).willReturn(availableBlocksByHairDresser);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "WORKER"})
    public void getFirstTenAvailableWorks() throws Exception {
        this.mockMvc.perform(get("/hairdresser/blocks/free?work=1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.*.*", hasSize(10)));
    }
}