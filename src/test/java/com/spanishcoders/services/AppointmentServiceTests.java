package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.model.*;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.repositories.AppointmentRepository;
import com.spanishcoders.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Created by agustin on 7/07/16.
 */
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
        Client user = mock(Client.class);
        given(userRepository.findByUsername(any(String.class))).willReturn(user);
    }

    @Test(expected = AccessDeniedException.class)
    public void confirmAppointmentNullAuthorization() {
        appointmentService.confirmAppointment(null, new AppointmentDTO());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentEmptyWorks() throws Exception {
        Authentication authentication = mock(Authentication.class);
        appointmentService.confirmAppointment(authentication, new AppointmentDTO());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentNullWorks() throws Exception {
        Authentication authentication = mock(Authentication.class);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setWorks(null);
        appointmentService.confirmAppointment(authentication, appointmentDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentEmptyBlocks() throws Exception {
        Authentication authentication = mock(Authentication.class);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(1);
        appointmentService.confirmAppointment(authentication, appointmentDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentNullBlocks() throws Exception {
        Authentication authentication = mock(Authentication.class);
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(1);
        appointmentDTO.setBlocks(null);
        appointmentService.confirmAppointment(authentication, appointmentDTO);
    }

    @Test(expected = AccessDeniedException.class)
    public void confirmAppointmentWithPrivateWorkAsClient() {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        Work privateWork = mock(Work.class);
        given(privateWork.getKind()).willReturn(WorkKind.PRIVATE);
        Block block = mock(Block.class);
        Set<Block> blocks = Sets.newHashSet(block);
        given(blockService.get(any(Collection.class))).willReturn(blocks);
        Set<Work> works = Sets.newHashSet(privateWork);
        given(workService.get(any(Set.class))).willReturn(works);
        AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
        appointmentService.confirmAppointment(authentication, appointmentDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentWithoutEnoughBlocks() {
        Authentication authentication = mock(Authentication.class);
        Work work = mock(Work.class);
        given(work.getDuration()).willReturn(60);
        Block block = mock(Block.class);
        given(block.getLength()).willReturn(Duration.of(30, ChronoUnit.MINUTES));
        Set<Block> blocks = Sets.newHashSet(block);
        given(blockService.get(any(Collection.class))).willReturn(blocks);
        Set<Work> works = Sets.newHashSet(work);
        given(workService.get(any(Set.class))).willReturn(works);
        AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
        appointmentService.confirmAppointment(authentication, appointmentDTO);
    }

    @Test
    public void confirmAppointment() {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        Work work = mock(Work.class);
        given(work.getDuration()).willReturn(30);
        given(work.getKind()).willReturn(WorkKind.PUBLIC);
        Block block = mock(Block.class);
        given(block.getLength()).willReturn(Duration.of(30, ChronoUnit.MINUTES));
        given(block.getStart()).willReturn(LocalTime.now());
        WorkingDay workingDay = mock(WorkingDay.class);
        given(workingDay.getDate()).willReturn(LocalDate.now());
        given(block.getWorkingDay()).willReturn(workingDay);
        given(appointmentRepository.save(any(Appointment.class))).willAnswer(invocation -> invocation.getArguments()[0]);
        Set<Work> requestedWorks = Sets.newTreeSet();
        requestedWorks.add(work);
        Set<Block> requestedBlocks = Sets.newTreeSet();
        requestedBlocks.add(block);
        given(blockService.get(any(Collection.class))).willReturn(requestedBlocks);
        given(workService.get(any(Set.class))).willReturn(requestedWorks);
        AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
        Appointment result = appointmentService.confirmAppointment(authentication, appointmentDTO);
        assertThat(result, notNullValue());
        assertThat(result.getWorks(), is(requestedWorks));
        assertThat(result.getBlocks(), is(requestedBlocks));
    }

    @Test
    public void cancelAppointmentWithLessThan24HoursAsWorker() throws Exception {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> workerAuthority = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> workerAuthority);
        Appointment appointment = mock(Appointment.class);
        given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(23));
        given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
            Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
            requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
            return requestedAppointment;
        });
        appointment = appointmentService.cancelAppointment(authentication, appointment);

        given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
        assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
    }

    @Test(expected = AccessDeniedException.class)
    public void cancelAppointmentWithLessThan24HoursAsClient() throws Exception {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        Appointment appointment = mock(Appointment.class);
        given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(23));
        appointmentService.cancelAppointment(authentication, appointment);
    }

    @Test(expected = AccessDeniedException.class)
    public void cancelAnotherClientAppointmentAsClient() throws Exception {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        Appointment appointment = mock(Appointment.class);
        given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
        appointmentService.cancelAppointment(authentication, appointment);
    }

    @Test
    public void cancelAnotherClientAppointmentAsWorker() throws Exception {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> workerAuthority = Sets.newHashSet(Role.WORKER.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> workerAuthority);
        Appointment appointment = mock(Appointment.class);
        given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
        given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
            Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
            requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
            return requestedAppointment;
        });
        appointment = appointmentService.cancelAppointment(authentication, appointment);

        given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
        assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
    }

    @Test
    public void cancelAppointmentWithMoreThan24HoursAsClient() throws Exception {
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        Appointment appointment = mock(Appointment.class);
        given(appointment.getDate()).willReturn(LocalDateTime.now().plusHours(25));
        given(appointmentRepository.save(any(Appointment.class))).will(invocation -> {
            Appointment requestedAppointment = (Appointment) invocation.getArguments()[0];
            requestedAppointment.setStatus(AppointmentStatus.CANCELLED);
            return requestedAppointment;
        });
        AppUser user = mock(AppUser.class);
        given(userRepository.findByUsername(any(String.class))).willReturn(user);
        given(appointment.getUser()).willReturn(user);
        given(appointment.getStatus()).willReturn(AppointmentStatus.CANCELLED);
        appointment = appointmentService.cancelAppointment(authentication, appointment);
        assertThat(appointment.getStatus(), is(AppointmentStatus.CANCELLED));
    }

    @Test
    public void getNextAppointmentsForExistingUser() {
        AppUser user = mock(AppUser.class);
        Appointment appointment = mock(Appointment.class);
        SortedSet<Appointment> appointments = Sets.newTreeSet();
        appointments.add(appointment);
        given(appointmentRepository.getNextAppointments(any(AppUser.class), any(AppointmentStatus.class))).willReturn(appointments);
        Set<Appointment> nextAppointments = appointmentService.getNextAppointments(user);
        assertThat(nextAppointments, not(empty()));
        assertThat(nextAppointments, hasItem(appointment));
    }

    @Test
    public void getNextAppointmentsForNonExistingUser() {
        AppUser user = mock(AppUser.class);
        SortedSet<Appointment> appointments = Sets.newTreeSet();
        given(appointmentRepository.getNextAppointments(any(AppUser.class), any(AppointmentStatus.class))).willReturn(appointments);
        Set<Appointment> nextAppointments = appointmentService.getNextAppointments(user);
        assertThat(nextAppointments, is(empty()));
    }
}