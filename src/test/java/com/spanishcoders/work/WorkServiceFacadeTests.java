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

public class WorkServiceFacadeTests extends PelukaapUnitTest {

	@MockBean
	private WorkService workService;

	@MockBean
	private WorkMapper workMapper;

	private WorkServiceFacade workServiceFacade;

	@Before
	public void setUp() {
		workServiceFacade = new WorkServiceFacade(workService, workMapper);
		when(workMapper.asDTOs(any(Set.class))).then(invocation -> {
			final Set<Work> works = invocation.getArgumentAt(0, Set.class);
			final Set<WorkDTO> dtos = Sets.newHashSet();
			for (final Work work : works) {
				final WorkDTO dto = new WorkDTO();
				dto.setId(work.getId());
				dtos.add(dto);
			}
			return dtos;
		});
	}

	@Test
	public void getWithoutAuthentication() {
		final Set<WorkDTO> workDTOs = workServiceFacade.get(null);
		assertThat(workDTOs, is(empty()));
	}

	@Test
	public void getWithoutData() {
		final Authentication authentication = mock(Authentication.class);
		when(workService.get(any(Authentication.class))).thenReturn(Sets.newHashSet());
		final Set<WorkDTO> workDTOs = workServiceFacade.get(authentication);
		assertThat(workDTOs, is(empty()));
	}

	@Test
	public void getWithData() {
		final Authentication authentication = mock(Authentication.class);
		final Work work = new Work();
		final Set<Work> works = Sets.newHashSet();
		works.add(work);
		when(workService.get(any(Authentication.class))).thenReturn(works);
		final Set<WorkDTO> workDTOs = workServiceFacade.get(authentication);
		assertThat(workDTOs, hasSize(1));
	}
}
