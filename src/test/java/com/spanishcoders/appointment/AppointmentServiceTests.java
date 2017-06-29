package com.spanishcoders.appointment;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserRepository;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkService;
import com.spanishcoders.work.WorkStatus;
import com.spanishcoders.workingday.WorkingDay;
import com.spanishcoders.workingday.block.Block;
import com.spanishcoders.workingday.block.BlockService;

public class AppointmentServiceTests extends PelukaapUnitTest {

	@MockBean
	private AppointmentRepository appointmentRepository;

	@MockBean
	private BlockService blockService;

	@MockBean
	private WorkService workService;

	@MockBean
	private UserRepository userRepository;

	private AppointmentService appointmentService;

	@Before
	public void setUp() throws Exception {
		appointmentService = new AppointmentService(appointmentRepository);
		final Client user = mock(Client.class);
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
	}

	@Test(expected = AccessDeniedException.class)
	public void confirmAppointmentNullUser() {
		appointmentService.createAppointment(null, null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentEmptyWorks() throws Exception {
		final AppUser user = mock(AppUser.class);
		appointmentService.createAppointment(user, Sets.newHashSet(), Sets.newHashSet(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentNullWorks() throws Exception {
		final AppUser user = mock(AppUser.class);
		appointmentService.createAppointment(user, Sets.newHashSet(), null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentEmptyBlocks() throws Exception {
		final AppUser user = mock(AppUser.class);
		appointmentService.createAppointment(user, Sets.newHashSet(), Sets.newHashSet(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentNullBlocks() throws Exception {
		final AppUser user = mock(AppUser.class);
		appointmentService.createAppointment(user, null, Sets.newHashSet(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentWithDisabledWork() {
		final AppUser user = mock(AppUser.class);
		final Work disabledWork = mock(Work.class);
		given(disabledWork.getStatus()).willReturn(WorkStatus.DISABLED);
		final Block block = mock(Block.class);
		final Set<Block> blocks = Sets.newHashSet(block);
		final Set<Work> works = Sets.newHashSet(disabledWork);
		appointmentService.createAppointment(user, blocks, works, null);
	}

	@Test(expected = AccessDeniedException.class)
	public void confirmAppointmentWithPrivateWorkAsClient() {
		final AppUser user = mock(Client.class);
		when(user.getRole()).thenReturn(Role.CLIENT);
		final Work privateWork = mock(Work.class);
		given(privateWork.getStatus()).willReturn(WorkStatus.ENABLED);
		when(privateWork.getKind()).thenReturn(WorkKind.PRIVATE);
		final Block block = mock(Block.class);
		final Set<Block> blocks = Sets.newHashSet(block);
		final Set<Work> works = Sets.newHashSet(privateWork);
		appointmentService.createAppointment(user, blocks, works, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentWithoutEnoughBlocks() {
		final AppUser user = mock(Client.class);
		final Work work = mock(Work.class);
		given(work.getStatus()).willReturn(WorkStatus.ENABLED);
		given(work.getDuration()).willReturn(Duration.ofMinutes(60));
		final Block block = mock(Block.class);
		given(block.getLength()).willReturn(Duration.ofMinutes(30));
		final Set<Block> blocks = Sets.newHashSet(block);
		final Set<Work> works = Sets.newHashSet(work);
		appointmentService.createAppointment(user, blocks, works, null);
	}

	@Test
	public void confirmAppointment() {
		final AppUser user = mock(Client.class);
		when(user.getRole()).thenReturn(Role.CLIENT);
		final Work work = mock(Work.class);
		given(work.getDuration()).willReturn(Duration.ofMinutes(30));
		given(work.getStatus()).willReturn(WorkStatus.ENABLED);
		given(work.getKind()).willReturn(WorkKind.PUBLIC);
		final Block block = mock(Block.class);
		given(block.getLength()).willReturn(Duration.of(30, ChronoUnit.MINUTES));
		given(block.getStart()).willReturn(LocalTime.now());
		final WorkingDay workingDay = mock(WorkingDay.class);
		given(workingDay.getDate()).willReturn(LocalDate.now());
		given(block.getWorkingDay()).willReturn(workingDay);
		given(appointmentRepository.save(any(Appointment.class)))
				.willAnswer(invocation -> invocation.getArguments()[0]);
		final Set<Work> requestedWorks = Sets.newTreeSet();
		requestedWorks.add(work);
		final Set<Block> requestedBlocks = Sets.newTreeSet();
		requestedBlocks.add(block);
		final Appointment result = appointmentService.createAppointment(user, requestedBlocks, requestedWorks, null);
		assertThat(result, notNullValue());
		assertThat(result.getWorks(), is(requestedWorks));
		assertThat(result.getBlocks(), is(requestedBlocks));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateNonExistingAppointment() {
		Appointment appointment = mock(Appointment.class);
		final AppUser user = mock(Hairdresser.class);
		when(appointmentRepository.findOne(any(Integer.class))).thenReturn(null);
		appointment = appointmentService.update(user, null, null, appointment);
	}

	@Test
	public void cancelAppointmentWithLessThan24HoursAsWorker() throws Exception {
		Appointment appointment = mock(Appointment.class);
		final AppUser user = mock(Hairdresser.class);
		when(user.getRole()).thenReturn(Role.WORKER);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(23));
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
			final Appointment requestedAppointment = mock(Appointment.class);
			when(requestedAppointment.getStatus()).thenReturn(AppointmentStatus.CANCELLED);
			return requestedAppointment;
		});
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		appointment = appointmentService.update(user, AppointmentStatus.CANCELLED, null, appointment);
		assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test(expected = AccessDeniedException.class)
	public void cancelAppointmentWithLessThan24HoursAsClient() throws Exception {
		final Appointment appointment = mock(Appointment.class);
		final AppUser user = mock(Client.class);
		when(user.getRole()).thenReturn(Role.CLIENT);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(23));
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		appointmentService.update(user, AppointmentStatus.CANCELLED, null, appointment);
	}

	@Test(expected = AccessDeniedException.class)
	public void cancelAnotherClientAppointmentAsClient() throws Exception {
		final Appointment appointment = mock(Appointment.class);
		final AppUser user = mock(Client.class);
		when(user.getRole()).thenReturn(Role.CLIENT);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		appointmentService.update(user, AppointmentStatus.CANCELLED, null, appointment);
	}

	@Test
	public void cancelAnotherClientAppointmentAsWorker() throws Exception {
		Appointment appointment = mock(Appointment.class);
		final AppUser user = mock(Hairdresser.class);
		when(user.getRole()).thenReturn(Role.WORKER);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
			final Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
			requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
			return requestedAppointment;
		});
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		appointment = appointmentService.update(user, AppointmentStatus.CANCELLED, null, appointment);
		assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test
	public void cancelAppointmentWithMoreThan24HoursAsClient() throws Exception {
		Appointment appointment = mock(Appointment.class);
		final AppUser user = mock(Client.class);
		when(user.getRole()).thenReturn(Role.CLIENT);
		when(appointmentRepository.findOne(any(Integer.class))).thenReturn(appointment);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
			final Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
			requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
			return requestedAppointment;
		});
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
		given(appointment.getUser()).willReturn(user);
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		appointment = appointmentService.update(user, AppointmentStatus.CANCELLED, null, appointment);
		assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test
	public void getNextAppointmentsForExistingUser() {
		final AppUser user = mock(AppUser.class);
		final Appointment appointment = mock(Appointment.class);
		final SortedSet<Appointment> appointments = Sets.newTreeSet();
		appointments.add(appointment);
		given(appointmentRepository.getNextAppointments(any(AppUser.class), any(AppointmentStatus.class)))
				.willReturn(appointments);
		final Set<Appointment> nextAppointments = appointmentService.getNextAppointments(user);
		assertThat(nextAppointments, not(empty()));
		assertThat(nextAppointments, hasItem(appointment));
	}

	@Test
	public void getNextAppointmentsForNonExistingUser() {
		final AppUser user = mock(AppUser.class);
		final SortedSet<Appointment> appointments = Sets.newTreeSet();
		given(appointmentRepository.getNextAppointments(any(AppUser.class), any(AppointmentStatus.class)))
				.willReturn(appointments);
		final Set<Appointment> nextAppointments = appointmentService.getNextAppointments(user);
		assertThat(nextAppointments, is(empty()));
	}
}