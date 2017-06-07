package com.spanishcoders.user.hairdresser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.workingday.block.Block;

public class HairdresserServiceTests extends PelukaapUnitTest {

	@MockBean
	private UserService userService;

	@MockBean
	private HairdresserRepository hairdresserRepository;

	@MockBean
	private AgendaService agendaService;

	private HairdresserService hairdresserService;

	@Before
	public void setUp() throws Exception {
		hairdresserService = new HairdresserService(userService, agendaService, hairdresserRepository);
	}

	@Test(expected = AccessDeniedException.class)
	public void registerHairdresserWithoutAuthentication() {
		hairdresserService.registerHairdresser(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerHairdresserWithoutData() {
		final Authentication authentication = mock(Authentication.class);
		hairdresserService.registerHairdresser(authentication, null);
	}

	@Test(expected = AccessDeniedException.class)
	public void registerHairdresserAsClient() {
		final Authentication authentication = mock(Authentication.class);
		final Collection authorities = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		when(authentication.getAuthorities()).thenReturn(authorities);
		final Hairdresser hairdresser = mock(Hairdresser.class);
		hairdresserService.registerHairdresser(authentication, hairdresser);
	}

	@Test
	public void registerHairdresser() {
		final Authentication authentication = mock(Authentication.class);
		final Collection authorities = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
		when(authentication.getAuthorities()).thenReturn(authorities);
		final Hairdresser hairdresser = mock(Hairdresser.class);
		when(hairdresser.getId()).thenReturn(1);
		when(hairdresser.getName()).thenReturn("hairdresser");
		when(userService.create(any(AppUser.class))).thenReturn(hairdresser);
		when(hairdresserRepository.findOne(any(Integer.class))).thenReturn(hairdresser);
		final Hairdresser newHairdresser = hairdresserService.registerHairdresser(authentication, hairdresser);
		assertThat(newHairdresser, is(hairdresser));
	}

	@Test
	public void getDayBlocksWithoutHairdressers() {
		given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet());
		final Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getDayBlocks(LocalDate.now());
		assertThat(availableBlocks.size(), is(0));
	}

	@Test
	public void getDayBlocksWithoutDay() {
		final Hairdresser hairdresser = mock(Hairdresser.class);
		given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
		final Set<Block> todaysBlocks = Sets.newHashSet();
		given(agendaService.getDayBlocks(any(Agenda.class), any(LocalDate.class))).willReturn(todaysBlocks);
		final Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getDayBlocks(null);
		assertThat(availableBlocks.entrySet(), not((empty())));
		assertThat(availableBlocks.get(hairdresser).size(), is((todaysBlocks.size())));
	}

	@Test
	public void getDayBlocksForToday() {
		final Hairdresser hairdresser = mock(Hairdresser.class);
		given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
		final Set<Block> todaysBlocks = Sets.newHashSet(mock(Block.class));
		given(agendaService.getDayBlocks(any(Agenda.class), any(LocalDate.class))).willReturn(todaysBlocks);
		final Map<Hairdresser, Set<Block>> availableBlocks = hairdresserService.getDayBlocks(LocalDate.now());
		assertThat(availableBlocks.entrySet(), not((empty())));
		assertThat(availableBlocks.get(hairdresser).size(), is((todaysBlocks.size())));
	}
}