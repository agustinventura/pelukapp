package com.spanishcoders.user.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;

public class ClientServiceFacadeTests extends PelukaapUnitTest {

	@MockBean
	private ClientService clientService;

	@MockBean
	private UserService userService;

	private final ClientMapper clientMapper = new ClientMapperImpl();

	private ClientServiceFacade clientServiceFacade;

	@Before
	public void setUp() {
		clientServiceFacade = new ClientServiceFacade(clientService, userService, clientMapper);
	}

	@Test
	public void createClientWithoutAuthentication() {
		when(clientService.createClient(any(Client.class)))
				.then(invocation -> invocation.getArgumentAt(0, Client.class));
		final ClientDTO client = getClientDTO();
		final ClientDTO created = clientServiceFacade.create(null, client);
		assertThat(created, is(client));
	}

	private ClientDTO getClientDTO() {
		final ClientDTO client = new ClientDTO();
		client.setName("test");
		return client;
	}

	@Test(expected = AccessDeniedException.class)
	public void createClientWithUnknownUser() {
		final Authentication authentication = mock(Authentication.class);
		when(userService.get(any(String.class))).thenReturn(Optional.empty());
		final ClientDTO client = getClientDTO();
		clientServiceFacade.create(authentication, client);
	}

	@Test
	public void createClientWithUser() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(Optional.of(user));
		when(clientService.createClient(any(AppUser.class), any(Client.class)))
				.then(invocation -> invocation.getArgumentAt(1, Client.class));
		final ClientDTO client = getClientDTO();
		final ClientDTO created = clientServiceFacade.create(authentication, client);
		assertThat(created, is(client));
	}
}
