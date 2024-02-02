package ua.goit.urlshortener.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsernameTest() {
        assertAll(
                () -> assertEquals("ADMIN", userRepository.findByUsername("testadmin").orElseThrow().getRole().name()),
                () -> assertEquals(Optional.empty(), userRepository.findByUsername("test"))
        );
    }

    @Test
    void existsByUsernameTest() {
        assertAll(
                () -> assertTrue(userRepository.existsByUsername("testadmin")),
                () -> assertFalse(userRepository.existsByUsername("test"))
        );
    }
}
