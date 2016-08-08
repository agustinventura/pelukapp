package com.spanishcoders.integration;

import com.spanishcoders.model.dto.BlockDTO;
import com.spanishcoders.model.dto.HairdresserDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by agustin on 8/08/16.
 */
public class HairdresserTests extends IntegrationTests {

    public static final String FREE_BLOCKS_URL = "/hairdresser/blocks/free/works=1";
    private HeadersTestRestTemplate<Map<HairdresserDTO, Set<BlockDTO>>> client;

    @Before
    public void setUp() {
        this.client = new HeadersTestRestTemplate<>(this.testRestTemplate);
    }

    @Test
    public void getAvailableBlocksWithoutAuthorization() {
        HeadersTestRestTemplate<String> errorClient = new HeadersTestRestTemplate<String>(testRestTemplate);
        String result = errorClient.getWithAuthorizationHeader(FREE_BLOCKS_URL, "", String.class);
        assertThat(result, containsString("403"));
    }

    @Test
    public void getAvailableBlocksAsClient() {
        String authHeader = loginAsClient();
        ParameterizedTypeReference<Map<HairdresserDTO, Set<BlockDTO>>> typeRef = new ParameterizedTypeReference<Map<HairdresserDTO, Set<BlockDTO>>>() {
        };
        Map<HairdresserDTO, Set<BlockDTO>> availableBlocks = client.getWithAuthorizationHeaderByType(FREE_BLOCKS_URL, authHeader, typeRef);
    }
}
