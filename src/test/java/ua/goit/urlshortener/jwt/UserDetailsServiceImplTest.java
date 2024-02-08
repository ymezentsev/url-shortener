package ua.goit.urlshortener.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserDetailsServiceImplTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserDetailsServiceImpl userDetailsService;


    @Test
    @DisplayName("Successful load user by username")
    void loadUserByUsernameTest() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testadmin");
        assertAll(
                () -> assertEquals("ADMIN",
                        userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority()),
                () -> assertEquals("{noop}qwerTy12", userDetails.getPassword())
        );
    }

    @Test
    @DisplayName("Fail load user by username")
    void loadUserByUsernameThrowUsernameNotFoundExceptionTest() {
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("test"));
    }
}