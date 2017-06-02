package com.spanishcoders.user.client;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.UserDTO;

@SpringBootTest
public class ClientControllerTests extends PelukaapUnitTest {

	@MockBean
	private ClientService clientService;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
		given(clientService.createClient(any(Authentication.class), any(Client.class))).will(invocation -> {
			return invocation.getArguments()[1];
		});
	}

	@Test
	@WithMockUser(username = "client", roles = { "USER", "CLIENT" })
	public void registerClient() throws Exception {
		final UserDTO dto = new UserDTO();
		dto.setUsername("client");
		this.mockMvc
				.perform(post("/client").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.username", is(dto.getUsername())));
	}

	@Test
	// TODO: FIX, THROWS 401
	public void registerNewClient() throws Exception {
		final UserDTO dto = new UserDTO();
		dto.setUsername("anonymous");
		this.mockMvc
				.perform(post("/client").content(toJSON(dto)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.username", is(dto.getUsername())));
	}
}
