package ua.goit.urlshortener.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.BadCredentialsException;
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
    @DisplayName("Successful searching user by username")
    void findByUsernameTest() {
        assertEquals("ADMIN", userService.findByUsername("testadmin").getRole().name());
    }

    @Test
    @DisplayName("Fail searching user by username")
    void findByUsernameThrowExceptionTest() {
        assertThrows(NoSuchElementException.class,
                () -> userService.findByUsername("test"));
    }

    @Test
    @DisplayName("Successful user registration")
    void registerUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("newUserTest", "Password9");

        userService.registerUser(createUserRequest);
        assertNotNull(userService.findByUsername("newUserTest"));
    }

    @Test
    @DisplayName("Fail user registration")
    void registerUserThrowExceptionTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testadmin", "qwerTy12");

        assertThrows(UserAlreadyExistException.class,
                () -> userService.registerUser(createUserRequest));
    }

    @Test
    @DisplayName("Successful user login")
    void loginUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testadmin", "qwerTy12");
        String jwtTokenRegex = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YWRtaW4iLCJBdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiVVNFUiJ9XSwiaWF0IjoxNzA1NjU0NTc2LCJleHAiOjE3MDU3NDA5NzZ9.OHofMISL71EBCuQp4uMjC2pjyyXn8kgktMO0Idaf7lg";

        String generatedJwtToken = userService.loginUser(createUserRequest);
        assertThat(generatedJwtToken.matches(jwtTokenRegex));
    }

    @Test
    @DisplayName("Fail user login")
    void loginUserThrowExceptionTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("test", "qwerTy12");

        assertThrows(BadCredentialsException.class,
                () -> userService.loginUser(createUserRequest));
    }
}