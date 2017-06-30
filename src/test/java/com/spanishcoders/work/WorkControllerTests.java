package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;

@WebMvcTest(controllers = WorkController.class)
public class WorkControllerTests extends PelukaapUnitTest {

	@MockBean
	private WorkServiceFacade workServiceFacade;

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		when(workServiceFacade.get(any(Authentication.class))).thenReturn(Sets.newHashSet(new WorkDTO()));
	}

	@Test
	public void getWorksWithoutAuthentication() throws Exception {
		this.mockMvc.perform(
				get("/works").contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getWorksAsWorker() throws Exception {
		this.mockMvc
				.perform(get("/works").contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.*", hasSize(1)));
	}

	@Test
	public void getWorkByIdWithoutAuthentication() throws Exception {
		this.mockMvc.perform(
				get("/works/1").contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getWorkByNonExistingIdAsWorker() throws Exception {
		when(workServiceFacade.get(any(Authentication.class), any(Integer.class))).thenReturn(null);
		this.mockMvc
				.perform(get("/works/" + Integer.MAX_VALUE).contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(content().string(is("")));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getWorkByIdAsWorker() throws Exception {
		final int workId = 1;
		final WorkDTO dto = new WorkDTO();
		dto.setId(workId);
		when(workServiceFacade.get(any(Authentication.class), any(Integer.class))).thenReturn(dto);
		this.mockMvc
				.perform(get("/works/" + workId).contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(workId)));
	}

	@Test
	public void createWorkWithoutAuthentication() throws Exception {
		final WorkDTO dto = new WorkDTO();
		this.mockMvc.perform(post("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void createWorkAsClient() throws Exception {
		final WorkDTO dto = new WorkDTO();
		this.mockMvc.perform(post("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void createWorkAsAdmin() throws Exception {
		final WorkDTO dto = new WorkDTO();
		dto.setName("test");
		dto.setDuration(Duration.ofMinutes(30L));
		dto.setWorkKind(WorkKind.PRIVATE);
		dto.setWorkStatus(WorkStatus.ENABLED);
		final int generatedId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		when(workServiceFacade.create(any(Authentication.class), any(WorkDTO.class))).then(invocation -> {
			final WorkDTO created = invocation.getArgumentAt(1, WorkDTO.class);
			created.setId(generatedId);
			return created;
		});
		this.mockMvc
				.perform(post("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", is(not(empty())))).andExpect(jsonPath("$.id", is(generatedId)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void createWorkWithInvalidDataAsAdmin() throws Exception {
		final WorkDTO dto = new WorkDTO();
		when(workServiceFacade.create(any(Authentication.class), any(WorkDTO.class)))
				.thenThrow(ConstraintViolationException.class);
		this.mockMvc.perform(post("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void updateWorkAsClient() throws Exception {
		final WorkDTO dto = new WorkDTO();
		this.mockMvc.perform(put("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void updateWorkAsAdmin() throws Exception {
		final WorkDTO dto = new WorkDTO();
		dto.setName("modified test");
		dto.setWorkKind(WorkKind.PRIVATE);
		dto.setWorkStatus(WorkStatus.ENABLED);
		dto.setId(1);
		when(workServiceFacade.update(any(Authentication.class), any(WorkDTO.class))).then(invocation -> {
			final WorkDTO modified = invocation.getArgumentAt(1, WorkDTO.class);
			return modified;
		});
		this.mockMvc
				.perform(put("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", is(not(empty())))).andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void updateWorkWithInvalidDataAsAdmin() throws Exception {
		final WorkDTO dto = new WorkDTO();
		when(workServiceFacade.update(any(Authentication.class), any(WorkDTO.class)))
				.thenThrow(IllegalArgumentException.class);
		this.mockMvc.perform(put("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isBadRequest());
	}
}