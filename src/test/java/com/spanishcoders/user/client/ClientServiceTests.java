package com.spanishcoders.user.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;

public class ClientServiceTests extends PelukaapUnitTest {

	@MockBean
	private UserService userService;

	private ClientService clientService;

	@Before
	public void setUp() {
		clientService = new ClientService(userService);
	}

	@Test
	public void clientRegistersHimself() {
		when(userService.create(any(Client.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		final Client client = mockClient();
		final Client savedClient = clientService.createClient(null, client);
		assertThat(client.getUsername(), is(savedClient.getUsername()));
	}

	private Client mockClient() {
		final Client client = mock(Client.class);
		when(client.getUsername()).thenReturn("client");
		return client;
	}

	@Test(expected = AccessDeniedException.class)
	public void clientRegistersClient() {
		final Authentication authentication = mock(Authentication.class);
		final Collection authorities = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		when(authentication.getAuthorities()).thenReturn(authorities);
		clientService.createClient(authentication, mock(Client.class));
	}

	@Test
	public void workerRegistersClient() {
		final Authentication authentication = mock(Authentication.class);
		final Collection authorities = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
		when(authentication.getAuthorities()).thenReturn(authorities);
		when(userService.create(any(Client.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		final Client client = mockClient();
		final Client savedClient = clientService.createClient(authentication, client);
		assertThat(client.getUsername(), is(savedClient.getUsername()));
	}
}