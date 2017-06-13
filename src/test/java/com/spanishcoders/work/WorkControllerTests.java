package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
		this.mockMvc.perform(get("/works").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$", is(not(empty()))));
	}
}