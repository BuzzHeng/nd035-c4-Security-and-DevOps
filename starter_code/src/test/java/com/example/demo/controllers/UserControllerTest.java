package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws  Exception{
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testpassword");
        r.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

        // Test findById method
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(u));
        final ResponseEntity<User> response2 = userController.findById(u.getId());
        assertNotNull(response2);
        assertEquals(200,response2.getStatusCodeValue());
        User u2 = response2.getBody();
        assertNotNull(u2);
        assertEquals(0, u2.getId());
        assertEquals("test", u2.getUsername());
        assertEquals("thisIsHashed", u2.getPassword());

        // Test findByUserName method
        when(userRepo.findByUsername(any(String.class))).thenReturn(u);
        final ResponseEntity<User> response3 = userController.findByUserName(u.getUsername());
        assertNotNull(response3);
        assertEquals(200, response3.getStatusCodeValue());
        User u3 = response3.getBody();
        assertNotNull(u3);
        assertEquals(0, u3.getId());
        assertEquals("test", u3.getUsername());
        assertEquals("thisIsHashed", u3.getPassword());
    }
}
