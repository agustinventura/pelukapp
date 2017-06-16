package com.spanishcoders.user.hairdresser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.schedule.HairdresserScheduleDTO;
import com.spanishcoders.workingday.schedule.ScheduleDTO;

@WebMvcTest(controllers = HairdresserController.class)
public class HairdresserControllerTests extends PelukaapUnitTest {

	@MockBean
	private HairdresserServiceFacade hairdresserServiceFacade;

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
		given(hairdresserServiceFacade.create(any(Authentication.class), any(Hairdresser.class))).will(invocation -> {
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
		given(hairdresserServiceFacade.create(any(Authentication.class), any(Hairdresser.class)))
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
		given(hairdresserServiceFacade.getSchedule(any(LocalDate.class))).willReturn(Sets.newHashSet());
		this.mockMvc
				.perform(get("/hairdresser/schedule/today")
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(0)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getTodayScheduleWithOneWork() throws Exception {
		given(hairdresserServiceFacade.getSchedule(any(LocalDate.class))).willAnswer(invocation -> {
			final LocalDate day = invocation.getArgumentAt(0, LocalDate.class);
			final ScheduleDTO schedule = mock(ScheduleDTO.class);
			when(schedule.getWorkingDay()).thenReturn(day);
			final Set<ScheduleDTO> schedules = Sets.newHashSet(schedule);
			final HairdresserDTO hairdresser = mock(HairdresserDTO.class);
			final HairdresserScheduleDTO hairdresserSchedule = new HairdresserScheduleDTO(hairdresser, schedules);
			final Set<HairdresserScheduleDTO> answer = Sets.newHashSet(hairdresserSchedule);
			return answer;
		});
		this.mockMvc
				.perform(get("/hairdresser/schedule/today")
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(1))).andExpect(jsonPath("$[0].schedule.*", hasSize(1)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getDayScheduleWithoutWorks() throws Exception {
		given(hairdresserServiceFacade.getSchedule(any(LocalDate.class))).willReturn(Sets.newHashSet());
		final LocalDate date = LocalDate.now();
		final String isoDate = date.format(DateTimeFormatter.ISO_DATE);
		this.mockMvc
				.perform(get("/hairdresser/schedule/" + isoDate)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(0)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getDayScheduleWithOneWork() throws Exception {
		given(hairdresserServiceFacade.getSchedule(any(LocalDate.class))).willAnswer(invocation -> {
			final LocalDate day = invocation.getArgumentAt(0, LocalDate.class);
			final ScheduleDTO schedule = mock(ScheduleDTO.class);
			when(schedule.getWorkingDay()).thenReturn(day);
			final Set<ScheduleDTO> schedules = Sets.newHashSet(schedule);
			final HairdresserDTO hairdresser = mock(HairdresserDTO.class);
			final HairdresserScheduleDTO hairdresserSchedule = new HairdresserScheduleDTO(hairdresser, schedules);
			final Set<HairdresserScheduleDTO> answer = Sets.newHashSet(hairdresserSchedule);
			return answer;
		});
		final LocalDate date = LocalDate.now();
		final String isoDate = date.format(DateTimeFormatter.ISO_DATE);
		this.mockMvc
				.perform(get("/hairdresser/schedule/" + isoDate)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(1))).andExpect(jsonPath("$[0].schedule.*", hasSize(1)));
	}
}