package com.spanishcoders.appointment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockService;

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
		answerAppointmentFromDTO();
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(1);
		appointmentDTO.getBlocks().add(1);
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.blocks", hasSize(1)))
				.andExpect(jsonPath("$.works", hasSize(1)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getAppointmentWithTwoWorks() throws Exception {
		answerAppointmentFromDTO();
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().addAll(Arrays.asList(1, 2));
		appointmentDTO.getBlocks().addAll(Arrays.asList(1, 2));
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.blocks", hasSize(2)))
				.andExpect(jsonPath("$.works", hasSize(2)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getAppointmentWithInvalidPairingWorkBlock() throws Exception {
		given(appointmentService.createAppointment(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(new IllegalArgumentException());
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void getAppointmentWithPrivateWorkAsClient() throws Exception {
		given(appointmentService.createAppointment(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(new AccessDeniedException("Access denied"));
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(post(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void cancelInvalidAppointment() throws Exception {
		given(appointmentService.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(IllegalArgumentException.class);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void cancelAppointmentWithMoreThan24Hours() throws Exception {
		given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
			final Optional<Appointment> appointment = Optional.of(new Appointment());
			return appointment;
		});
		given(appointmentService.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final Appointment appointment = new Appointment();
					appointment.setStatus(AppointmentStatus.CANCELLED);
					return appointment;
				});
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.status", is(1)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void cancelAppointmentAsWorker() throws Exception {
		given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
			final Optional<Appointment> appointment = Optional.of(new Appointment());
			return appointment;
		});
		given(appointmentService.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final Appointment appointment = new Appointment();
					appointment.setStatus(AppointmentStatus.CANCELLED);
					return appointment;
				});
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.status", is(1)));
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void cancelAppointmentWithLessThan24HoursAsClient() throws Exception {
		given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
			final Optional<Appointment> appointment = Optional.of(new Appointment());
			return appointment;
		});
		given(appointmentService.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willThrow(AccessDeniedException.class);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void cancelAppointmentWithMoreThan24HoursAsClient() throws Exception {
		given(appointmentService.get(any(Integer.class))).willAnswer(invocation -> {
			final Optional<Appointment> appointment = Optional.of(new Appointment());
			return appointment;
		});
		given(appointmentService.update(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final Appointment appointment = new Appointment();
					appointment.setStatus(AppointmentStatus.CANCELLED);
					return appointment;
				});
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		this.mockMvc
				.perform(put(APPOINTMENT_URL).content(toJSON(appointmentDTO)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.*", hasSize(8))).andExpect(jsonPath("$.status", is(1)));
	}

	private void answerAppointmentFromDTO() {
		given(appointmentService.createAppointment(any(Authentication.class), any(AppointmentDTO.class)))
				.willAnswer(invocation -> {
					final AppointmentDTO appointmentDTO = (AppointmentDTO) invocation.getArguments()[1];
					final Appointment appointment = new Appointment();
					appointment.setBlocks(appointmentDTO.getBlocks().stream().map(blockId -> {
						final Block block = new Block();
						block.setId(blockId);
						final WorkingDay workingDay = mock(WorkingDay.class);
						given(workingDay.getDate()).willReturn(LocalDate.now());
						block.setWorkingDay(workingDay);
						return block;
					}).collect(Collectors.toSet()));
					appointment.setWorks(appointmentDTO.getWorks().stream().map(workId -> {
						final Work work = new Work();
						work.setId(workId);
						work.setDuration(Duration.ofMinutes(30L));
						return work;
					}).collect(Collectors.toSet()));
					return appointment;
				});
	}
}