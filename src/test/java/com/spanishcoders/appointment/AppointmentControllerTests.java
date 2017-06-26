package com.spanishcoders.appointment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockService;

@WebMvcTest(controllers = AppointmentController.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.spanishcoders.error.*") })
public class AppointmentControllerTests extends PelukaapUnitTest {

	public static final String APPOINTMENT_URL = "/appointment";

	@MockBean
	private AppointmentServiceFacade appointmentServiceFacade;

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
			final Set<Integer> workIds = (Set<Integer>) invocation.getArguments()[0];
			final Set<Work> works = workIds.stream().map(id -> {
				final Work work = new Work();
				work.setId(id);
				return work;
			}).collect(Collectors.toSet());
			return works;
		});
		given(blockService.get(any(Set.class))).willAnswer(invocation -> {
			final Set<Integer> blockIds = (Set<Integer>) invocation.getArguments()[0];
			final Set<Block> blocks = blockIds.stream().map(id -> {
				final Block block = new Block();
				block.setId(id);
				return block;
			}).collect(Collectors.toSet());
			return blocks;
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getAppointmentWithOneWork() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(1);
		appointmentDTO.getBlocks().add(1);
		answerCreatedAppointment();
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isCreated()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.blocks", hasSize(1)))
				.andExpect(jsonPath("$.works", hasSize(1)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getAppointmentWithInvalidPairingWorkBlock() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		given(appointmentServiceFacade.create(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(IllegalArgumentException.class);
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void getAppointmentWithPrivateWorkAsClient() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		given(appointmentServiceFacade.create(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(AccessDeniedException.class);
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void changeAppointmentNotes() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.setNotes("notes");
		answerModifiedAppointment();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.notes", is(appointmentDTO.getNotes())));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void changeInvalidAppointmentNotes() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.setNotes("notes");
		answerInvalidAppointmentModification();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void cancelInvalidAppointment() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		answerInvalidAppointmentModification();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isBadRequest());
	}

	private void answerInvalidAppointmentModification() {
		given(appointmentServiceFacade.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(IllegalArgumentException.class);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void cancelAppointmentWithMoreThanMaxHoursToCancelAsWorker() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		answerCancelledAppointment();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.status", is("CANCELLED")));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void cancelAppointmentWithLessThanMaxHoursToCancelAsWorker() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		answerCancelledAppointment();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.status", is("CANCELLED")));
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void cancelAppointmentWithLessThanMaxHoursToCancelAsClient() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		given(appointmentServiceFacade.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(AccessDeniedException.class);
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void cancelAppointmentWithMoreThanMaxHoursToCancelAsClient() throws Exception {
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		answerCancelledAppointment();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.status", is("CANCELLED")));
	}

	private void answerModifiedAppointment() {
		given(appointmentServiceFacade.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final AppointmentDTO appointmentDTO = (AppointmentDTO) invocation.getArguments()[1];
					return appointmentDTO;
				});
	}

	private void answerCancelledAppointment() {
		given(appointmentServiceFacade.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final AppointmentDTO appointmentDTO = (AppointmentDTO) invocation.getArguments()[1];
					appointmentDTO.setStatus(AppointmentStatus.CANCELLED);
					return appointmentDTO;
				});
	}

	private void answerCreatedAppointment() {
		given(appointmentServiceFacade.create(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final AppointmentDTO appointmentDTO = (AppointmentDTO) invocation.getArguments()[1];
					return appointmentDTO;
				});
	}
}