package com.spanishcoders.user.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.UserService;
import com.spanishcoders.user.hairdresser.Hairdresser;

public class ClientServiceTests extends PelukaapUnitTest {

	@MockBean
	private UserService userService;

	@MockBean
	private ClientRepository clientRepository;

	private ClientService clientService;

	@Before
	public void setUp() {
		clientService = new ClientService(userService, clientRepository);
	}

	@Test
	public void clientRegistersHimself() {
		when(userService.create(any(Client.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		final Client client = getClient();
		when(clientRepository.findOne(any(Integer.class))).thenReturn(client);
		final Client savedClient = clientService.createClient(client);
		assertThat(client, is(savedClient));
	}

	private Client getClient() {
		final Client client = new Client();
		client.setUsername("test");
		return client;
	}

	@Test(expected = AccessDeniedException.class)
	public void clientRegistersClient() {
		final Client loggedClient = new Client();
		clientService.createClient(loggedClient, getClient());
	}

	@Test
	public void workerRegistersClient() {
		final Hairdresser hairdresser = new Hairdresser();
		final Client client = getClient();
		when(userService.create(any(Client.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		when(clientRepository.findOne(any(Integer.class))).thenReturn(client);
		final Client savedClient = clientService.createClient(hairdresser, client);
		assertThat(client.getUsername(), is(savedClient.getUsername()));
	}
}