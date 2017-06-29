package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.validation.ConstraintViolationException;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;

public class WorkServiceFacadeTests extends PelukaapUnitTest {

	@MockBean
	private WorkService workService;

	private final WorkMapper workMapper = new WorkMapperImpl();

	@MockBean
	private UserService userService;

	private WorkServiceFacade workServiceFacade;

	@Before
	public void setUp() {
		workServiceFacade = new WorkServiceFacade(workService, workMapper, userService);
	}

	@Test
	public void getWithoutAuthentication() {
		final Set<WorkDTO> workDTOs = workServiceFacade.get(null);
		assertThat(workDTOs, is(empty()));
	}

	@Test
	public void getWithoutUser() {
		final Authentication authentication = mock(Authentication.class);
		when(userService.get(any(String.class))).thenReturn(null);
		final Set<WorkDTO> workDTOs = workServiceFacade.get(authentication);
		assertThat(workDTOs, is(empty()));
	}

	@Test
	public void getWithoutData() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(user);
		when(workService.get(any(AppUser.class))).thenReturn(Sets.newHashSet());
		final Set<WorkDTO> workDTOs = workServiceFacade.get(authentication);
		assertThat(workDTOs, is(empty()));
	}

	@Test
	public void getWithData() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(userService.get(any(String.class))).thenReturn(user);
		final Work work = new Work();
		final Set<Work> works = Sets.newHashSet();
		works.add(work);
		when(workService.get(any(AppUser.class))).thenReturn(works);
		final Set<WorkDTO> workDTOs = workServiceFacade.get(authentication);
		assertThat(workDTOs, hasSize(1));
	}

	@Test(expected = AccessDeniedException.class)
	public void createAsClient() {
		final Authentication authentication = mock(Authentication.class);
		final Client client = mock(Client.class);
		when(client.getRole()).thenReturn(Role.CLIENT);
		when(userService.get(any(String.class))).thenReturn(client);
		workServiceFacade.create(authentication, new WorkDTO());
	}

	@Test
	public void createAsWorker() {
		final Authentication authentication = mock(Authentication.class);
		final Hairdresser worker = mock(Hairdresser.class);
		when(worker.getRole()).thenReturn(Role.WORKER);
		when(userService.get(any(String.class))).thenReturn(worker);
		final WorkDTO dto = new WorkDTO();
		dto.setName("test");
		dto.setDuration(Duration.ofMinutes(30L));
		dto.setWorkKind(WorkKind.PRIVATE);
		dto.setWorkStatus(WorkStatus.ENABLED);
		final int generatedId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		when(workService.create(any(Work.class))).then(invocation -> {
			final Work created = invocation.getArgumentAt(0, Work.class);
			created.setId(generatedId);
			return created;
		});
		final WorkDTO created = workServiceFacade.create(authentication, dto);
		assertThat(created, is(dto));
		assertThat(created.getId(), is(generatedId));
	}

	private Object workFromDto(final WorkDTO dto) {
		final Work work = new Work();
		work.setDuration(dto.getDuration());
		work.setKind(dto.getWorkKind());
		work.setName(dto.getName());
		work.setStatus(dto.getWorkStatus());
		return work;
	}

	private WorkDTO dtoFromWork(final Work work) {
		final WorkDTO createdDto = new WorkDTO();
		createdDto.setDuration(work.getDuration());
		createdDto.setId(work.getId());
		createdDto.setName(work.getName());
		createdDto.setWorkKind(work.getKind());
		createdDto.setWorkStatus(work.getStatus());
		return createdDto;
	}

	@Test(expected = AccessDeniedException.class)
	public void updateAsClient() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(user.getRole()).thenReturn(Role.CLIENT);
		when(userService.get(any(String.class))).thenReturn(user);
		workServiceFacade.update(authentication, new WorkDTO());
	}

	@Test
	public void updateAsWorker() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(user.getRole()).thenReturn(Role.WORKER);
		when(userService.get(any(String.class))).thenReturn(user);
		when(workService.get(any(Integer.class))).then(invocation -> {
			return Optional.of(new Work());
		});
		final WorkDTO dto = new WorkDTO();
		when(workService.update(any(String.class), any(WorkKind.class), any(WorkStatus.class), any(Work.class)))
				.then(invocation -> {
					return invocation.getArgumentAt(3, Work.class);
				});
		final WorkDTO updated = workServiceFacade.update(authentication, dto);
		assertThat(updated, is(dto));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateNonExistingAsAdmin() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(user.getRole()).thenReturn(Role.WORKER);
		when(userService.get(any(String.class))).thenReturn(user);
		when(workService.get(any(Integer.class))).then(invocation -> {
			return Optional.empty();
		});
		when(workService.update(any(String.class), any(WorkKind.class), any(WorkStatus.class), any(Work.class)))
				.thenThrow(IllegalArgumentException.class);
		final WorkDTO updated = workServiceFacade.update(authentication, new WorkDTO());
	}

	@Test(expected = ConstraintViolationException.class)
	public void updateWithInvalidDataAsAdmin() {
		final Authentication authentication = mock(Authentication.class);
		final AppUser user = mock(AppUser.class);
		when(user.getRole()).thenReturn(Role.WORKER);
		when(userService.get(any(String.class))).thenReturn(user);
		when(workService.get(any(Integer.class))).then(invocation -> {
			return Optional.of(new Work());
		});
		when(workService.update(any(String.class), any(WorkKind.class), any(WorkStatus.class), any(Work.class)))
				.thenThrow(ConstraintViolationException.class);
		final WorkDTO updated = workServiceFacade.update(authentication, new WorkDTO());
	}
}
