package ua.goit.urlshortener.url;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UrlRepository urlRepository;

    @Test
    @DisplayName("Find url by user's id")
    void findByUserIdTest() {
        assertAll(
                () -> assertEquals(2, urlRepository.findByUserId(2L).size()),
                () -> assertEquals(0, urlRepository.findByUserId(10L).size())
        );
    }

    @Test
    @DisplayName("Find url by short name")
    void findByShortUrlTest() {
        assertAll(
                () -> assertEquals("for test only1",
                        urlRepository.findByShortUrl("testurl1").orElseThrow().getDescription()),
                () -> assertEquals(Optional.empty(), urlRepository.findByShortUrl("test"))
        );
    }

    @Test
    @DisplayName("Find all active urls for current user")
    void findActiveUrlsByUserIdTest() {
        assertAll(
                () -> assertEquals(2,
                        urlRepository.findActiveUrlsByUserId(2L, LocalDate.now()).size()),
                () -> assertEquals(1,
                        urlRepository.findActiveUrlsByUserId(4L, LocalDate.now()).size())
        );
    }

    @Test
    @DisplayName("Find all inactive urls for current user")
    void findInactiveUrlsByUserIdTest() {
        assertEquals(0, urlRepository.findInactiveUrlsByUserId(2L, LocalDate.now()).size());
    }

    @Test
    @DisplayName("Find all active urls")
    void findActiveUrlsTest() {
        assertEquals(4, urlRepository.findActiveUrls(LocalDate.now()).size());
    }

    @Test
    @DisplayName("Find all inactive urls")
    void findInactiveUrlsTest() {
        assertEquals(0, urlRepository.findInactiveUrls(LocalDate.now()).size());
    }
}