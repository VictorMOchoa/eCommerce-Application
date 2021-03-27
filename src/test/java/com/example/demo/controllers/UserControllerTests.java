package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser_Test() {
        // Stub
        String username = "test_user";
        String password = "testPassword";
        String hashedPassword = "hashed-stub";

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        // Mock
        when(encoder.encode(password)).thenReturn(hashedPassword);

        // Test
        ResponseEntity<User> response =  userController.createUser(request);

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(0, response.getBody().getId());
        Assert.assertEquals(username, response.getBody().getUsername());
        Assert.assertEquals(hashedPassword, response.getBody().getPassword());
    }

    @Test
    public void createUser_BadPasswordLength_Fail() {
        // Stub
        String username = "test_user";
        String password = "123";
        String hashedPassword = "hashed-stub";

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        // Mock
        when(encoder.encode(password)).thenReturn(hashedPassword);

        // Test
        ResponseEntity<User> response =  userController.createUser(request);;

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatusCode().value());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void createUser_MismatchPassword_Fail() {
        // Stub
        String username = "test_user";
        String password = "test-password";
        String confirmPassword = "incorrect-confirm";
        String hashedPassword = "hashed-stub";

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);

        // Mock
        when(encoder.encode(password)).thenReturn(hashedPassword);

        // Test
        ResponseEntity<User> response =  userController.createUser(request);;

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatusCode().value());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void findByUserName_Success_Test() {
        // Stub
        User user = createStubUser();
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        // Test
        ResponseEntity<User> response = userController.findByUserName("test-user");

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(0, response.getBody().getId());
        Assert.assertEquals(user, response.getBody());
    }

    private User createStubUser() {
        User testUser = new User();
        testUser.setUsername("test-user");
        testUser.setPassword("password");
        testUser.setId(0);
        return testUser;
    }
}
