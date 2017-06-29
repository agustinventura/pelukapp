package com.spanishcoders.work;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.common.collect.Sets;
import com.spanishcoders.PelukaapUnitTest;
import com.spanishcoders.user.AppUser;
import com.spanishcoders.user.Role;

public class WorkServiceTests extends PelukaapUnitTest {

	@MockBean
	private WorkRepository workRepository;

	@MockBean
	private AppUser user;

	private WorkService workService;

	@Before
	public void setUp() {
		workService = new WorkService(workRepository);
	}

	@Test
	public void getAvailableWorksForWorker() throws Exception {
		when(workRepository.findAll()).thenReturn(Sets.newHashSet(new Work()));
		when(user.getRole()).thenReturn(Role.WORKER);
		final Set<Work> availableWorks = workService.get(user);
		assertThat(availableWorks, hasSize(1));
	}

	@Test
	public void getAvailableWorksForClient() throws Exception {
		when(workRepository.findByKindAndStatus(WorkKind.PUBLIC, WorkStatus.ENABLED))
				.thenReturn(Sets.newHashSet(new Work()));
		when(user.getRole()).thenReturn(Role.CLIENT);
		final Set<Work> availableWorks = workService.get(user);
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

	@Test
	public void createWork() {
		final int generatedId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		final Work work = new Work();
		work.setDuration(Duration.ofMinutes(30L));
		work.setName("test");
		when(workRepository.save(any(Work.class))).then(invocation -> {
			final Work created = invocation.getArgumentAt(0, Work.class);
			created.setId(generatedId);
			return created;
		});
		final Work created = workService.create(work);
		assertThat(created, is(work));
		assertThat(created.getId(), is(generatedId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateNonExistingWork() {
		when(workRepository.findOne(any(Integer.class))).thenReturn(null);
		workService.update(null, null, null, new Work());
	}

	@Test
	public void updateWork() {
		final Work work = new Work();
		work.setId(1);
		final String name = "test";
		final WorkKind kind = WorkKind.PRIVATE;
		final WorkStatus status = WorkStatus.DISABLED;
		when(workRepository.findOne(any(Integer.class))).thenReturn(work);
		when(workRepository.save(any(Work.class))).then(invocation -> {
			final Work argument = invocation.getArgumentAt(0, Work.class);
			return argument;
		});
		final Work modified = workService.update(name, kind, status, work);
		assertThat(modified.getName(), is(name));
		assertThat(modified.getKind(), is(kind));
		assertThat(modified.getStatus(), is(status));
	}
}