package ua.goit.urlshortener.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.userdetails.UserDetails;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class JwtUtilsTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Generate jwt token")
    void generateJwtTokenTest() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser1");
        assertNotNull(jwtUtils.generateJwtToken(userDetails));
    }

    @Test
    @DisplayName("Get username from jwt token")
    void getUsernameFromJwtTokenTest() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser1");
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        assertEquals("testuser1", jwtUtils.getUsernameFromJwtToken(jwtToken));
    }

    @Test
    @DisplayName("Successful validate jwt token")
    void validateJwtTokenTest() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser1");
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        assertTrue(jwtUtils.validateJwtToken(jwtToken));
    }

    @Test
    @DisplayName("Fail validate jwt token")
    void validateJwtTokenFailTest() {
        assertFalse(jwtUtils.validateJwtToken("wrongToken"));
    }
}