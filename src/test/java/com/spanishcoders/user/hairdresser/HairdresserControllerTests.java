package com.spanishcoders.user.hairdresser;

import static com.spanishcoders.TestDataFactory.mockAllWorks;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;

@WebMvcTest(controllers = HairdresserController.class)
public class HairdresserControllerTests extends PelukaapUnitTest {

	@MockBean
	private HairdresserService hairdresserService;

	@MockBean
	private WorkService workService;

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		given(workService.get(any(Set.class))).willReturn(mockAllWorks());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getScheduleForTheDayWithoutWorks() throws Exception {
		given(hairdresserService.getDayBlocks(any(LocalDate.class))).willReturn(Maps.newHashMap());
		this.mockMvc
				.perform(get("/hairdresser/schedule/today")
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(0)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getScheduleForTheDayWithOneWork() throws Exception {
		given(hairdresserService.getDayBlocks(any(LocalDate.class))).willAnswer(invocation -> {
			final Map<Hairdresser, Set<Block>> answer = Maps.newHashMap();
			final Block block = mock(Block.class);
			final WorkingDay workingDay = mock(WorkingDay.class);
			final Agenda agenda = mock(Agenda.class);
			final Hairdresser hairdresser = mock(Hairdresser.class);
			given(hairdresser.getId()).willReturn(1);
			given(hairdresser.getStatus()).willReturn(UserStatus.ACTIVE);
			given(agenda.getHairdresser()).willReturn(hairdresser);
			given(workingDay.getAgenda()).willReturn(agenda);
			given(workingDay.getDate()).willReturn(LocalDate.now());
			given(block.getStart()).willReturn(LocalTime.now());
			given(block.getLength()).willReturn(Block.DEFAULT_BLOCK_LENGTH);
			given(block.getWorkingDay()).willReturn(workingDay);
			final Set<Block> blocks = Sets.newHashSet(block);
			answer.put(hairdresser, blocks);
			return answer;
		});
		this.mockMvc
				.perform(get("/hairdresser/schedule/today")
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(1))).andExpect(jsonPath("$[0].schedule.*", hasSize(1)));
	}
}