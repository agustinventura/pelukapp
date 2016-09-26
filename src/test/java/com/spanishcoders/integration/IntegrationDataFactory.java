package com.spanishcoders.integration;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.AppointmentDTO;
import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserAvailableBlocks;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.spanishcoders.integration.AppointmentTests.APPOINTMENT_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by agustin on 11/08/16.
 */
public class IntegrationDataFactory {

    private TestRestTemplate testRestTemplate;

    public IntegrationDataFactory(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    public Set<Work> getWorks(String auth) {
        HeadersTestRestTemplate<Set<Work>> worksClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<Set<Work>> worksTypeRef = new ParameterizedTypeReference<Set<Work>>() {
        };
        return Sets.newTreeSet(worksClient.getWithAuthorizationHeader(WorkTests.WORKS_URL, auth, worksTypeRef));
    }

    public String getWorksUrl(String auth) {
        return getWorks(auth).stream().map(work -> work.getId().toString()).collect(Collectors.joining(";works=", "works=", ""));
    }

    public Set<BlockDTO> getBlocks(String auth, Set<Work> works) {
        return this.getBlocks(auth, works, LocalDate.now());
    }

    public Set<BlockDTO> getBlocks(String auth, Set<Work> works, LocalDate date) {
        HeadersTestRestTemplate<List<HairdresserAvailableBlocks>> blocksClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<List<HairdresserAvailableBlocks>> blocksTypeRef = new ParameterizedTypeReference<List<HairdresserAvailableBlocks>>() {
        };
        String blocksUrl = HairdresserTests.FREE_BLOCKS_URL;
        if (date != null) {
            blocksUrl += date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/";
        }
        blocksUrl += works.stream().map(work -> work.getId().toString()).collect(Collectors.joining(";works=", "works=", ""));
        List<HairdresserAvailableBlocks> hairdresserAvailableBlocks = blocksClient.getWithAuthorizationHeader(blocksUrl, auth, blocksTypeRef);
        return Sets.newTreeSet(hairdresserAvailableBlocks.get(0).getAvailableBlocks());
    }

    public AppointmentDTO getAppointment(String auth) {
        return getAppointment(auth, LocalDate.now());
    }

    public AppointmentDTO getAppointment(String auth, LocalDate date) {
        TreeSet<Work> works = (TreeSet<Work>) this.getWorks(auth);
        Work work = works.first();
        TreeSet<BlockDTO> blocks = null;
        blocks = (TreeSet<BlockDTO>) this.getBlocks(auth, works, date);
        if (blocks.size() == 0) {
            blocks = (TreeSet<BlockDTO>) this.getBlocks(auth, works, date.plusDays(1));
        }
        BlockDTO block = blocks.last();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.getWorks().add(work.getId());
        appointmentDTO.getBlocks().add(block.getId());
        HeadersTestRestTemplate<AppointmentDTO> appointmentsClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<AppointmentDTO> appointmentsTypeRef = new ParameterizedTypeReference<AppointmentDTO>() {
        };
        AppointmentDTO confirmed = appointmentsClient.postWithAuthorizationHeader(APPOINTMENT_URL, auth, appointmentDTO, appointmentsTypeRef);
        assertThat(confirmed, notNullValue());
        assertThat(confirmed.getId(), notNullValue());
        assertThat(confirmed.getWorks(), is(appointmentDTO.getWorks()));
        assertThat(confirmed.getBlocks(), is(appointmentDTO.getBlocks()));
        return confirmed;
    }
}
