package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;

public class WorkServiceTests extends PelukaapUnitTest {

	@MockBean
	private WorkRepository workRepository;

	@MockBean
	private Authentication authentication;

	private WorkService workService;

	@Before
	public void setUp() {
		workService = new WorkService(workRepository);
	}

	@Test
	public void getAvailableWorksForWorker() throws Exception {
		final Collection authorities = mock(Collection.class);
		when(workRepository.findAll()).thenReturn(Sets.newHashSet(new Work()));
		when(authentication.getAuthorities()).thenReturn(authorities);
		when(authorities.contains(any(GrantedAuthority.class))).thenReturn(true);
		final Set<Work> availableWorks = workService.get(authentication);
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getAvailableWorksForClient() throws Exception {
		final Collection authorities = mock(Collection.class);
		when(workRepository.findByKind(WorkKind.PUBLIC)).thenReturn(Sets.newHashSet(new Work()));
		when(authentication.getAuthorities()).thenReturn(authorities);
		when(authorities.contains(any(GrantedAuthority.class))).thenReturn(false);
		final Set<Work> availableWorks = workService.get(authentication);
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getAvailableWorksWithoutAuthorities() throws Exception {
		when(workRepository.findByKind(WorkKind.PUBLIC)).thenReturn(Sets.newHashSet(new Work()));
		when(authentication.getAuthorities()).thenReturn(Collections.EMPTY_SET);
		final Set<Work> availableWorks = workService.get(authentication);
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getWorksById() {
		when(workRepository.findAll(any(Collection.class))).thenReturn(Sets.newHashSet(new Work()));
		final Set<Work> works = workService.get(Sets.newHashSet(1));
		assertThat(works, hasSize(1));
	}

	@Test
	public void getWorksByEmptyId() {
		final Set<Work> works = workService.get(Sets.newHashSet());
		assertThat(works, is(empty()));
	}
}