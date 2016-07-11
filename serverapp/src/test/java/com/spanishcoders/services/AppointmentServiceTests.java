package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.*;
import com.spanishcoders.repositories.AppointmentRepository;
import com.spanishcoders.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * Created by agustin on 7/07/16.
 */
@RunWith(SpringRunner.class)
public class AppointmentServiceTests {

    @MockBean
    private AppointmentRepository appointmentRepository;

    @MockBean
    private UserRepository userRepository;

    private AppointmentService appointmentService;

    @Before
    public void setUp() throws Exception {
        appointmentService = new AppointmentService(appointmentRepository, userRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentNullAuthorization() {
        appointmentService.confirmAppointment(null, Sets.newHashSet(), Sets.newHashSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentEmptyWorks() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        appointmentService.confirmAppointment(authentication, Sets.newHashSet(), Sets.newHashSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentNullWorks() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        appointmentService.confirmAppointment(authentication, null, Sets.newHashSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentEmptyBlocks() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Work work = Mockito.mock(Work.class);
        appointmentService.confirmAppointment(authentication, Sets.newHashSet(work), Sets.newHashSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentNullBlocks() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Work work = Mockito.mock(Work.class);
        appointmentService.confirmAppointment(authentication, Sets.newHashSet(work), null);
    }

    @Test(expected = AccessDeniedException.class)
    public void confirmAppointmentWithPrivateWorkAsClient() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        Work privateWork = Mockito.mock(Work.class);
        given(privateWork.getKind()).willReturn(WorkKind.PRIVATE);
        Block block = Mockito.mock(Block.class);
        appointmentService.confirmAppointment(authentication, Sets.newHashSet(privateWork), Sets.newHashSet(block));
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmAppointmentWithoutEnoughBlocks() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Work work = Mockito.mock(Work.class);
        given(work.getDuration()).willReturn(60);
        Block block = Mockito.mock(Block.class);
        given(block.getLength()).willReturn(Duration.of(30, ChronoUnit.MINUTES));
        appointmentService.confirmAppointment(authentication, Sets.newHashSet(work), Sets.newHashSet(block));
    }

    @Test
    public void confirmAppointment() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Collection<GrantedAuthority> clientAuthority = Sets.newHashSet(Role.CLIENT.getGrantedAuthority());
        given(authentication.getAuthorities()).willAnswer(invocation -> clientAuthority);
        User user = Mockito.mock(User.class);
        given(userRepository.findByUsername(any(String.class))).willReturn(user);
        Work work = Mockito.mock(Work.class);
        given(work.getDuration()).willReturn(30);
        given(work.getKind()).willReturn(WorkKind.PUBLIC);
        Block block = Mockito.mock(Block.class);
        given(block.getLength()).willReturn(Duration.of(30, ChronoUnit.MINUTES));
        given(block.getStart()).willReturn(LocalTime.now());
        WorkingDay workingDay = Mockito.mock(WorkingDay.class);
        given(workingDay.getDate()).willReturn(LocalDate.now());
        given(block.getWorkingDay()).willReturn(workingDay);
        given(appointmentRepository.save(any(Appointment.class))).willAnswer(invocation -> invocation.getArguments()[0]);
        Set<Work> requestedWorks = Sets.newTreeSet();
        requestedWorks.add(work);
        Set<Block> requestedBlocks = Sets.newTreeSet();
        requestedBlocks.add(block);
        Appointment result = appointmentService.confirmAppointment(authentication, requestedWorks, requestedBlocks);
        assertThat(result, notNullValue());
        assertThat(result.getWorks(), is(requestedWorks));
        assertThat(result.getBlocks(), is(requestedBlocks));
    }
}