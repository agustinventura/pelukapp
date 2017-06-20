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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserRepository;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkService;
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
		appointmentService = new AppointmentService(appointmentRepository, blockService, workService, userRepository);
		final Client user = mock(Client.class);
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
	}

	@Test(expected = AccessDeniedException.class)
	public void confirmAppointmentNullAuthorization() {
		appointmentService.createAppointment(null, new AppointmentDTO());
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentEmptyWorks() throws Exception {
		final Authentication authentication = mock(Authentication.class);
		appointmentService.createAppointment(authentication, new AppointmentDTO());
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentNullWorks() throws Exception {
		final Authentication authentication = mock(Authentication.class);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().clear();
		appointmentService.createAppointment(authentication, appointmentDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentEmptyBlocks() throws Exception {
		final Authentication authentication = mock(Authentication.class);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(1);
		appointmentService.createAppointment(authentication, appointmentDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentNullBlocks() throws Exception {
		final Authentication authentication = mock(Authentication.class);
		final AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.getWorks().add(1);
		appointmentDTO.getBlocks().clear();
		appointmentService.createAppointment(authentication, appointmentDTO);
	}

	@Test(expected = AccessDeniedException.class)
	public void confirmAppointmentWithPrivateWorkAsClient() {
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
		final Work privateWork = mock(Work.class);
		given(privateWork.getKind()).willReturn(WorkKind.PRIVATE);
		final Block block = mock(Block.class);
		final Set<Block> blocks = Sets.newHashSet(block);
		given(blockService.get(any(Collection.class))).willReturn(blocks);
		final Set<Work> works = Sets.newHashSet(privateWork);
		given(workService.get(any(Set.class))).willReturn(works);
		final AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
		appointmentService.createAppointment(authentication, appointmentDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void confirmAppointmentWithoutEnoughBlocks() {
		final Authentication authentication = mock(Authentication.class);
		final Work work = mock(Work.class);
		given(work.getDuration()).willReturn(Duration.ofMinutes(60));
		final Block block = mock(Block.class);
		given(block.getLength()).willReturn(Duration.ofMinutes(30));
		final Set<Block> blocks = Sets.newHashSet(block);
		given(blockService.get(any(Collection.class))).willReturn(blocks);
		final Set<Work> works = Sets.newHashSet(work);
		given(workService.get(any(Set.class))).willReturn(works);
		final AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
		appointmentService.createAppointment(authentication, appointmentDTO);
	}

	@Test
	public void confirmAppointment() {
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
		final Work work = mock(Work.class);
		given(work.getDuration()).willReturn(Duration.ofMinutes(30));
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
		given(blockService.get(any(Collection.class))).willReturn(requestedBlocks);
		given(workService.get(any(Set.class))).willReturn(requestedWorks);
		final AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
		final Appointment result = appointmentService.createAppointment(authentication, appointmentDTO);
		assertThat(result, notNullValue());
		assertThat(result.getWorks(), is(requestedWorks));
		assertThat(result.getBlocks(), is(requestedBlocks));
	}

	@Test
	public void cancelAppointmentWithLessThan24HoursAsWorker() throws Exception {
		Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> workerAuthority = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> workerAuthority);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(23));
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
			final Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
			requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
			return requestedAppointment;
		});
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		appointment = appointmentService.update(authentication, new AppointmentDTO());
		assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test(expected = AccessDeniedException.class)
	public void cancelAppointmentWithLessThan24HoursAsClient() throws Exception {
		final Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(23));
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		appointmentService.update(authentication, new AppointmentDTO());
	}

	@Test(expected = AccessDeniedException.class)
	public void cancelAnotherClientAppointmentAsClient() throws Exception {
		final Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		appointmentService.update(authentication, new AppointmentDTO());
	}

	@Test
	public void cancelAnotherClientAppointmentAsWorker() throws Exception {
		Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> workerAuthority = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> workerAuthority);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
			final Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
			requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
			return requestedAppointment;
		});
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		appointment = appointmentService.update(authentication, new AppointmentDTO());
		assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test
	public void cancelAppointmentWithMoreThan24HoursAsClient() throws Exception {
		Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
		given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
			final Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
			requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
			return requestedAppointment;
		});
		final AppUser user = mock(AppUser.class);
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
		given(appointment.getUser()).willReturn(user);
		given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
		appointment = appointmentService.update(authentication, new AppointmentDTO());
		assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
	}

	@Test
	public void modifyAppointmentNotesAsWorker() throws Exception {
		Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> workerAuthority = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> workerAuthority);
		given(appointment.getDate()).willReturn(LocalDateTime.now());
		given(appointment.getStatus()).willReturn(AppointmentStatus.VALID);
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> invocation.getArguments()[0]);
		final String notes = "new notes";
		given(appointment.getNotes()).willReturn(notes);
		appointment = appointmentService.update(authentication, new AppointmentDTO());
		assertThat(appointment.getNotes(), is(notes));
	}

	@Test
	public void modifyAppointmentNotesAsClient() throws Exception {
		final AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
		given(appointmentDTO.getStatus()).willReturn(AppointmentStatus.VALID);
		Appointment appointment = mock(Appointment.class);
		given(appointmentRepository.findOne(any(Integer.class))).willReturn(appointment);
		final Authentication authentication = mock(Authentication.class);
		final Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
		given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
		given(appointmentRepository.save(any(Appointment.class))).will(invocation -> invocation.getArguments()[0]);
		final AppUser user = mock(AppUser.class);
		given(userRepository.findByUsername(any(String.class))).willReturn(user);
		given(appointment.getDate()).willReturn(LocalDateTime.now());
		given(appointment.getUser()).willReturn(user);
		final String notes = "new notes";
		given(appointmentDTO.getNotes()).willReturn(notes);
		given(appointment.getStatus()).willReturn(AppointmentStatus.VALID);
		given(appointment.getNotes()).willReturn(notes);
		appointment = appointmentService.update(authentication, appointmentDTO);
		assertThat(appointment.getNotes(), is(notes));
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