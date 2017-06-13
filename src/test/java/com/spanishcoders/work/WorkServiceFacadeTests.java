package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
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
	}

	@Test
	public void getWithNullAuthentication() {
		when(workService.get(any(Authentication.class))).thenReturn(Sets.newHashSet());
		when(workMapper.asDTOs(any(Set.class))).then(invocation -> {
			final Set<Work> works = invocation.getArgumentAt(0, Set.class);
			final Set<WorkDTO> dtos = Sets.newHashSet();
			for (final Work work : works) {
				dtos.add(new WorkDTO(work));
			}
			return dtos;
		});
		final Set<WorkDTO> workDTOs = workServiceFacade.get(null);
		assertThat(workDTOs, is(empty()));
	}
}
