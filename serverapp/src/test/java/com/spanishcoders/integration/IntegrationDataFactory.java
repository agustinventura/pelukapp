package com.spanishcoders.integration;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Work;
import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserAvailableBlocks;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        HeadersTestRestTemplate<List<HairdresserAvailableBlocks>> blocksClient = new HeadersTestRestTemplate<>(testRestTemplate);
        ParameterizedTypeReference<List<HairdresserAvailableBlocks>> blocksTypeRef = new ParameterizedTypeReference<List<HairdresserAvailableBlocks>>() {
        };
        String blocksUrl = HairdresserTests.FREE_BLOCKS_URL + works.stream().map(work -> work.getId().toString()).collect(Collectors.joining(";works=", "works=", ""));
        List<HairdresserAvailableBlocks> hairdresserAvailableBlocks = blocksClient.getWithAuthorizationHeader(blocksUrl, auth, blocksTypeRef);
        return Sets.newTreeSet(hairdresserAvailableBlocks.get(0).getAvailableBlocks());
    }
}
