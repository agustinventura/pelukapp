package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
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
import com.spanishcoders.user.Role;
import com.spanishcoders.user.UserService;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;

public class WorkServiceFacadeTests extends PelukaapUnitTest {

	@MockBean
	private WorkService workService;

	@MockBean
	private WorkMapper workMapper;

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
		when(workMapper.asDTOs(any(Set.class))).then(invocation -> {
			final Set<Work> works = invocation.getArgumentAt(0, Set.class);
			final Set<WorkDTO> dtos = Sets.newHashSet();
			for (final Work work : works) {
				final WorkDTO dto = new WorkDTO();
				dto.setId(work.getId());
				dto.setDuration(work.getDuration());
				dto.setWorkKind(work.getKind());
				dto.setWorkStatus(work.getStatus());
				dtos.add(dto);
			}
			return dtos;
		});
		final Work work = new Work();
		final Set<Work> works = Sets.newHashSet();
		works.add(work);
		when(workService.get(any(AppUser.class))).thenReturn(works);
		final Set<WorkDTO> workDTOs = workServiceFacade.get(authentication);
		assertThat(workDTOs, hasSize(1));
	}
	
	@Test(expected = AccessDeniedException.class)
	public void createAsClient() {
		Authentication authentication = mock(Authentication.class);
		Client client = mock(Client.class);
		when(client.getRole()).thenReturn(Role.CLIENT);
		when(userService.get(any(String.class))).thenReturn(client);
		workServiceFacade.create(authentication, new WorkDTO());
	}
	
	@Test
	public void createAsWorker() {
		Authentication authentication = mock(Authentication.class);
		Hairdresser worker = mock(Hairdresser.class);
		when(worker.getRole()).thenReturn(Role.WORKER);
		when(userService.get(any(String.class))).thenReturn(worker);
		when(workMapper.asWork(any(WorkDTO.class))).then(invocation -> {
			WorkDTO dto = invocation.getArgumentAt(0, WorkDTO.class);
			Work work = new Work();
			work.setDuration(dto.getDuration());
			work.setKind(dto.getWorkKind());
			work.setName(dto.getName());
			work.setStatus(dto.getWorkStatus());
			return work;
		});
		final WorkDTO dto = new WorkDTO();
		dto.setName("test");
		dto.setDuration(Duration.ofMinutes(30L));
		dto.setWorkKind(WorkKind.PRIVATE);
		dto.setWorkStatus(WorkStatus.ENABLED);
		final int generatedId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		when(workService.create(any(Work.class))).then(invocation -> {
			Work created = invocation.getArgumentAt(0, Work.class);
			created.setId(generatedId);
			return created;
		});
		when(workMapper.asDTO(any(Work.class))).then(invocation -> {
			Work work = invocation.getArgumentAt(0, Work.class);
			WorkDTO createdDto = new WorkDTO();
			createdDto.setDuration(work.getDuration());
			createdDto.setId(work.getId());
			createdDto.setName(work.getName());
			createdDto.setWorkKind(work.getKind());
			createdDto.setWorkStatus(work.getStatus());
			return createdDto;
		});
		WorkDTO created = workServiceFacade.create(authentication, dto);
		assertThat(created, is(dto));
		assertThat(created.getId(), is(generatedId));
	}
}
