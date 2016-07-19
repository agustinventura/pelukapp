package com.spanishcoders.services;

import com.spanishcoders.repositories.UserRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceTests {

    @MockBean
    private UserRepository userRepository;

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService(userRepository);
    }


}