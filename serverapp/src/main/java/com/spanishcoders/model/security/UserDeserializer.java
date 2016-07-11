package com.spanishcoders.model.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.spanishcoders.model.User;

import java.io.IOException;

/**
 * Created by agustin on 11/07/16.
 */
public class UserDeserializer extends JsonDeserializer<User> {
    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String username = node.get("username").asText();
        String password = node.get("password").asText();
        return new User(username, password);
    }
}
