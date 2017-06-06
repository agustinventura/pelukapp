package com.spanishcoders.user.hairdresser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void registerWorkerAsWorker() throws Exception {
		given(hairdresserService.registerHairdresser(any(Authentication.class), any(Hairdresser.class)))
				.will(invocation -> {
					return invocation.getArguments()[1];
				});
		final HairdresserDTO dto = new HairdresserDTO();
		dto.setUsername("client");
		this.mockMvc
				.perform(post("/hairdresser").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.username", is(dto.getUsername())));
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void registerWorkerAsClient() throws Exception {
		given(hairdresserService.registerHairdresser(any(Authentication.class), any(Hairdresser.class)))
				.willThrow(AccessDeniedException.class);
		final HairdresserDTO dto = new HairdresserDTO();
		dto.setUsername("client");
		this.mockMvc
				.perform(post("/hairdresser").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isForbidden());
	}

	@Test
	public void registerWorkerAsAnonymous() throws Exception {
		final HairdresserDTO dto = new HairdresserDTO();
		dto.setUsername("client");
		this.mockMvc
				.perform(post("/hairdresser").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getTodayScheduleWithoutWorks() throws Exception {
		given(hairdresserService.getDayBlocks(any(LocalDate.class))).willReturn(Maps.newHashMap());
		this.mockMvc
				.perform(get("/hairdresser/schedule/today")
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(0)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getTodayScheduleWithOneWork() throws Exception {
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