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
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.AgendaService;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.schedule.Schedule;

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
		final Hairdresser hairdresser = new Hairdresser();
		hairdresserService.registerHairdresser(hairdresser, null);
	}

	@Test(expected = AccessDeniedException.class)
	public void registerHairdresserAsClient() {
		final AppUser client = new Client();
		final Hairdresser hairdresser = getHairdresser();
		hairdresserService.registerHairdresser(client, hairdresser);
	}

	private Hairdresser getHairdresser() {
		final Hairdresser hairdresser = new Hairdresser();
		hairdresser.setUsername("test");
		return hairdresser;
	}

	@Test
	public void registerHairdresser() {
		final Hairdresser hairdresser = getHairdresser();
		when(userService.create(any(AppUser.class))).thenReturn(hairdresser);
		when(hairdresserRepository.findOne(any(Integer.class))).thenReturn(hairdresser);
		final Hairdresser newHairdresser = hairdresserService.registerHairdresser(new Hairdresser(), hairdresser);
		assertThat(newHairdresser, is(hairdresser));
	}

	@Test
	public void getDayBlocksWithoutHairdressers() {
		given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet());
		final Set<Schedule> schedules = hairdresserService.getSchedule(LocalDate.now());
		assertThat(schedules.size(), is(0));
	}

	@Test
	public void getDayBlocksWithoutDay() {
		final Hairdresser hairdresser = mock(Hairdresser.class);
		given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
		final Set<Block> todaysBlocks = Sets.newHashSet();
		given(agendaService.getDayBlocks(any(Agenda.class), any(LocalDate.class))).willReturn(todaysBlocks);
		final Set<Schedule> schedules = hairdresserService.getSchedule(null);
		assertThat(schedules, not((empty())));
		assertThat(schedules.iterator().next().getBlocks().size(), is((todaysBlocks.size())));
	}

	@Test
	public void getDayBlocksForToday() {
		final Hairdresser hairdresser = mock(Hairdresser.class);
		given(hairdresserRepository.findByStatus(any(UserStatus.class))).willReturn(Sets.newHashSet(hairdresser));
		final Set<Block> todaysBlocks = Sets.newHashSet(mock(Block.class));
		given(agendaService.getDayBlocks(any(Agenda.class), any(LocalDate.class))).willReturn(todaysBlocks);
		final Set<Schedule> schedules = hairdresserService.getSchedule(LocalDate.now());
		assertThat(schedules, not((empty())));
		assertThat(schedules.iterator().next().getBlocks().size(), is((todaysBlocks.size())));
	}
}