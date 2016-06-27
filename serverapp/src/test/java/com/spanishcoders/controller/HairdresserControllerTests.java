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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.spanishcoders.model.Block.DEFAULT_BLOCK_LENGTH;
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
        mockFindAllWorks();
        mockGetFirstTenAvailableBlocks();
    }

    private void mockGetFirstTenAvailableBlocks() {
        Set<Block> testBlocks = createMockBlocks();
        Map<Hairdresser, Set<Block>> availableBlocksByHairDresser = createMockBlocksByHairdresser(testBlocks);
        given(hairdresserService.getFirstTenAvailableBlocksByHairdresser(any(Set.class))).willReturn(availableBlocksByHairDresser);
    }

    private Map<Hairdresser, Set<Block>> createMockBlocksByHairdresser(Set<Block> testBlocks) {
        Map<Hairdresser, Set<Block>> availableBlocksByHairDresser = Maps.newHashMap();
        Hairdresser hairdresser = new Hairdresser("admin", "admin", "phone");
        availableBlocksByHairDresser.put(hairdresser, testBlocks);
        return availableBlocksByHairDresser;
    }

    private Set<Block> createMockBlocks() {
        LocalTime startTime = LocalTime.of(9, 00);
        Set<Block> testBlocks = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            startTime = startTime.plus(DEFAULT_BLOCK_LENGTH);
            testBlocks.add(new Block(startTime, null));
        }
        return testBlocks;
    }

    private void mockFindAllWorks() {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        given(workRepository.findAll(any(Collection.class))).willReturn(Sets.newHashSet(cut));
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