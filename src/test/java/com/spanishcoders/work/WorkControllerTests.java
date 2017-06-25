package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.access.AccessDeniedException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

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
		given(workServiceFacade.get(any(Authentication.class))).willReturn(Sets.newHashSet(new WorkDTO()));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void getWorksAdmin() throws Exception {
		this.mockMvc
		.perform(get("/works").contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.*", hasSize(1)));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "WORKER" })
	public void createWork() throws Exception {
		final WorkDTO dto = new WorkDTO();
		dto.setName("test");
		dto.setDuration(Duration.ofMinutes(30L));
		dto.setWorkKind(WorkKind.PRIVATE);
		dto.setWorkStatus(WorkStatus.ENABLED);
		final int generatedId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		when(workServiceFacade.create(any(Authentication.class), any(WorkDTO.class))).then(invocation -> {
			WorkDTO created = invocation.getArgumentAt(1, WorkDTO.class);
			created.setId(generatedId);
			return created;
		});
		this.mockMvc.perform(post("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", is(not(empty())))).andExpect(jsonPath("$.id", is(generatedId)));
	}
	
	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void createWorkAsClient() throws Exception {
		final WorkDTO dto = new WorkDTO();
		when(workServiceFacade.create(any(Authentication.class), any(WorkDTO.class))).thenThrow(AccessDeniedException.class);
		this.mockMvc
		.perform(post("/works").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(status().isUnauthorized());
	}
}