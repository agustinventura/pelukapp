package com.spanishcoders.user.hairdresser;

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
import com.spanishcoders.workingday.schedule.ScheduleMapper;
import com.spanishcoders.workingday.schedule.ScheduleMapperImpl;

public class HairdresserServiceFacadeTests extends PelukaapUnitTest {

	@MockBean
	private HairdresserService hairdresserService;

	@MockBean
	private UserService userService;

	private final HairdresserMapper hairdresserMapper = new HairdresserMapperImpl();

	private HairdresserServiceFacade hairdresserServiceFacade;

	private final ScheduleMapper scheduleMapper = new ScheduleMapperImpl();

	@Before
	public void setUp() {
		hairdresserServiceFacade = new HairdresserServiceFacade(hairdresserService, userService, hairdresserMapper,
				scheduleMapper);
	}

	private HairdresserDTO getHairdresserDTO() {
		final HairdresserDTO hairdresser = new HairdresserDTO();
		hairdresser.setName("test");
		return hairdresser;
	}

	@Test(expected = AccessDeniedException.class)
	public void createHairdresserWithUnknownUser() {
		final Authentication authentication = mock(Authentication.class);
		when(userService.get(any(String.class))).thenReturn(Optional.empty());
		final HairdresserDTO hairdresser = getHairdresserDTO();
		hairdresserServiceFacade.create(authentication, hairdresser);
	}

	@Test
	public void createHairdresserWithUser() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(Optional.of(user));
		when(hairdresserService.registerHairdresser(any(AppUser.class), any(Hairdresser.class)))
				.then(invocation -> invocation.getArgumentAt(1, Hairdresser.class));
		final HairdresserDTO hairdresser = getHairdresserDTO();
		final HairdresserDTO created = hairdresserServiceFacade.create(authentication, hairdresser);
		assertThat(created, is(hairdresser));
	}
}
