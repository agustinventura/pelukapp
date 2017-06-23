package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.UserService;

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
}
