package com.spanishcoders.integration;

import org.junit.Test;

/**
 * Created by agustin on 6/08/16.
 */
public class UserTests extends IntegrationTests {

    @Test
    public void clientLogin() {
        loginAsClient();
    }

    @Test
    public void adminLogin() {
        loginAsAdmin();
    }

    @Test
    public void registerUser() {
        String json = "";

        loginAs(json);
    }
}
