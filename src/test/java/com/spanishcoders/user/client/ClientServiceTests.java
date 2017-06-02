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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.Role;

public class ClientServiceTests extends PelukaapUnitTest {

	@MockBean
	private ClientRepository clientRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	private ClientService clientService;

	@Before
	public void setUp() {
		clientService = new ClientService(clientRepository, passwordEncoder);
	}

	@Test(expected = IllegalArgumentException.class)
	public void clientRegistersExistingUsername() {
		when(clientRepository.findByUsername(any(String.class))).thenReturn(mock(Client.class));
		clientService.createClient(null, mock(Client.class));
	}

	@Test
	public void clientRegistersHimself() {
		when(clientRepository.findByUsername(any(String.class))).thenReturn(null);
		when(passwordEncoder.encode(any(CharSequence.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
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
		when(clientRepository.findByUsername(any(String.class))).thenReturn(null);
		when(passwordEncoder.encode(any(CharSequence.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
			return invocation.getArguments()[0];
		});
		final Client client = mockClient();
		final Client savedClient = clientService.createClient(authentication, client);
		assertThat(client.getUsername(), is(savedClient.getUsername()));
	}
}