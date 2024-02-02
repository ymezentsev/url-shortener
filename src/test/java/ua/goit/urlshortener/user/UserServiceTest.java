package ua.goit.urlshortener.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserService userService;

    @Test
    void findByUsernameTest() {
        assertEquals("ADMIN", userService.findByUsername("testadmin").getRole().name());
    }

    @Test
    void findByUsernameThrowExceptionTest() {
        assertThrows(NoSuchElementException.class,
                () -> userService.findByUsername("test"),
                "There isn't user with username: test");
    }

    @Test
    void registerUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newUserTest");
        createUserRequest.setPassword("Password9");

        userService.registerUser(createUserRequest);
        assertNotNull(userService.findByUsername("newUserTest"));
    }

    @Test
    void registerUserThrowExceptionTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testadmin");
        createUserRequest.setPassword("qwerTy12");

        assertThrows(UserAlreadyExistException.class,
                () -> userService.registerUser(createUserRequest));
    }

    @Test
    void loginUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testadmin");
        createUserRequest.setPassword("qwerTy12");

        String jwtTokenRegex = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YWRtaW4iLCJBdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiVVNFUiJ9XSwiaWF0IjoxNzA1NjU0NTc2LCJleHAiOjE3MDU3NDA5NzZ9.OHofMISL71EBCuQp4uMjC2pjyyXn8kgktMO0Idaf7lg";

        String generatedJwtToken = userService.loginUser(createUserRequest);
        assertThat(generatedJwtToken.matches(jwtTokenRegex));
    }
}