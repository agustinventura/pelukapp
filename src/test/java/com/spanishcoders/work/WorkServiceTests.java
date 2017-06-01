package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.Role;

public class WorkServiceTests extends PelukaapUnitTest {

	@MockBean
	private WorkRepository workRepository;

	private WorkService workService;

	@Before
	public void setUp() {
		workService = new WorkService(workRepository);
	}

	@Test
	public void getAvailableWorksForWorker() throws Exception {
		given(workRepository.findAll()).willReturn(Sets.newHashSet(new Work()));
		final Set<Work> availableWorks = workService
				.getAvailableWorks(Sets.newHashSet(Role.WORKER.getGrantedAuthority()));
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getAvailableWorksForClient() throws Exception {
		given(workRepository.findByKind(WorkKind.PUBLIC)).willReturn(Sets.newHashSet(new Work()));
		final Set<Work> availableWorks = workService
				.getAvailableWorks(Sets.newHashSet(Role.CLIENT.getGrantedAuthority()));
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getAvailableWorksWithoutAuthorities() throws Exception {
		given(workRepository.findByKind(WorkKind.PUBLIC)).willReturn(Sets.newHashSet(new Work()));
		final Set<Work> availableWorks = workService.getAvailableWorks(Sets.newHashSet());
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getAvailableWorksWithNullAuthorities() throws Exception {
		given(workRepository.findByKind(WorkKind.PUBLIC)).willReturn(Sets.newHashSet(new Work()));
		final Set<Work> availableWorks = workService.getAvailableWorks(null);
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getWorksById() {
		given(workRepository.findAll(any(Collection.class))).willReturn(Sets.newHashSet(new Work()));
		final Set<Work> works = workService.get(Sets.newHashSet(1));
		assertThat(works, hasSize(1));
	}

	@Test
	public void getWorksByEmptyId() {
		final Set<Work> works = workService.get(Sets.newHashSet());
		assertThat(works, is(empty()));
	}

	@Test
	public void getWorksByNullId() {
		final Set<Work> works = workService.get(null);
		assertThat(works, is(empty()));
	}

}