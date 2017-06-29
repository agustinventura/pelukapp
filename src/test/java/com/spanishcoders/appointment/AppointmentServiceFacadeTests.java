package com.spanishcoders.appointment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockService;

public class AppointmentServiceFacadeTests extends PelukaapUnitTest {

	@MockBean
	private AppointmentService appointmentService;

	private final AppointmentMapper appointmentMapper = new AppointmentMapperImpl();

	@MockBean
	private BlockService blockService;

	@MockBean
	private WorkService workService;

	@MockBean
	private UserService userService;

	private AppointmentServiceFacade appointmentServiceFacade;

	@Before
	public void setUp() {
		this.appointmentServiceFacade = new AppointmentServiceFacade(appointmentService, appointmentMapper,
				blockService, workService, userService);
	}

	@Test(expected = AccessDeniedException.class)
	public void createAppointmentWithoutUser() {
		appointmentServiceFacade.create(null, new AppointmentDTO());
	}

	@Test(expected = AccessDeniedException.class)
	public void createAppointmentWithInvalidUser() {
		final Authentication authentication = mock(Authentication.class);
		when(userService.get(any(String.class))).thenReturn(Optional.empty());
		appointmentServiceFacade.create(authentication, new AppointmentDTO());
	}

	@Test
	public void createAppointment() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(Optional.of(user));
		final Set<Block> blocks = Sets.newHashSet();
		when(blockService.get(any(Collection.class))).thenReturn(blocks);
		final Set<Work> works = Sets.newHashSet();
		when(workService.get(any(Set.class))).thenReturn(works);
		final int generatedId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		when(appointmentService.createAppointment(any(AppUser.class), any(Set.class), any(Set.class),
				any(String.class))).then(invocation -> {
					final AppUser appointmentUser = invocation.getArgumentAt(0, AppUser.class);
					final Set<Block> appointmentBlocks = invocation.getArgumentAt(1, Set.class);
					final Set<Work> appointmentWorks = invocation.getArgumentAt(2, Set.class);
					final String appointmentNotes = invocation.getArgumentAt(3, String.class);
					final Appointment appointment = new Appointment();
					appointment.setUser(appointmentUser);
					appointment.setBlocks(appointmentBlocks);
					appointment.setWorks(appointmentWorks);
					appointment.setNotes(appointmentNotes);
					appointment.setId(generatedId);
					return appointment;
				});
		final AppointmentDTO appointment = appointmentServiceFacade.create(authentication, new AppointmentDTO());
		assertThat(appointment.getId(), is(generatedId));
	}

	@Test(expected = AccessDeniedException.class)
	public void updateAppointmentWithoutUser() {
		appointmentServiceFacade.update(null, new AppointmentDTO());
	}

	@Test(expected = AccessDeniedException.class)
	public void updateAppointmentWithInvalidUser() {
		final Authentication authentication = mock(Authentication.class);
		when(userService.get(any(String.class))).thenReturn(Optional.empty());
		appointmentServiceFacade.update(authentication, new AppointmentDTO());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateNonExistingAppointment() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(Optional.of(user));
		when(appointmentService.get(any(Integer.class))).thenReturn(Optional.empty());
		appointmentServiceFacade.update(authentication, new AppointmentDTO());
	}

	@Test
	public void updateAppointment() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(Optional.of(user));
		final Appointment appointment = new Appointment();
		when(appointmentService.get(any(Integer.class))).thenReturn(Optional.of(appointment));
		when(appointmentService.update(any(AppUser.class), any(AppointmentStatus.class), any(String.class),
				any(Appointment.class))).then(invocation -> {
					final AppUser apptUser = invocation.getArgumentAt(0, AppUser.class);
					final AppointmentStatus status = invocation.getArgumentAt(1, AppointmentStatus.class);
					final String notes = invocation.getArgumentAt(2, String.class);
					final Appointment appt = invocation.getArgumentAt(3, Appointment.class);
					appt.setUser(apptUser);
					appt.setStatus(status);
					appt.setNotes(notes);
					return appt;
				});
		final AppointmentDTO dto = new AppointmentDTO();
		dto.setStatus(AppointmentStatus.CANCELLED);
		dto.setNotes("test");
		final AppointmentDTO modified = appointmentServiceFacade.update(authentication, dto);
		assertThat(modified.getStatus(), is(dto.getStatus()));
		assertThat(modified.getNotes(), is(dto.getNotes()));
	}
}
