package com.spanishcoders;

import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spanishcoders.configuration.development.DevelopmentSecurityConfiguration;

@RunWith(SpringRunner.class)
@ActiveProfiles("development")
@Import(DevelopmentSecurityConfiguration.class)
public abstract class PelukaapUnitTest {

	protected String toJSON(Object dto) throws JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		return objectMapper.writeValueAsString(dto);
	}

}
