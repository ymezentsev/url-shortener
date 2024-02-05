package ua.goit.urlshortener.url.service;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.goit.urlshortener.DBInitializer;
import ua.goit.urlshortener.url.UrlDto;
import ua.goit.urlshortener.url.UrlEntity;
import ua.goit.urlshortener.url.exceptions.AlreadyExistUrlException;
import ua.goit.urlshortener.url.exceptions.NotAccessibleException;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UrlService urlService;

    @Autowired
    DBInitializer dbInitializer;

    @Mock
    HttpServletResponse response;

    @BeforeEach
    void setUp() {
        dbInitializer.cleanAndPopulateUrlTable();
    }

    @Test
    void getAllTest() {
        assertEquals(4, urlService.getAll().size());
    }

    @Test
    void createUrlTest() {
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://google.com", "valid url");
        Authentication authentication = getAuthentication("ADMIN");

        assertAll(
                () -> assertNotNull(urlService.createUrl(createUrlRequest, authentication).getShortUrl()),
                () -> assertEquals(5, urlService.getAll().size())
        );
    }

    @Test
    void createUrlThrowExceptionTest() {
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://wrongUrl.com", "incorrect url");
        Authentication authentication = getAuthentication("ADMIN");

        assertThrows(NotAccessibleException.class,
                () -> urlService.createUrl(createUrlRequest, authentication));
    }

    @Test
    void deleteByIdTest() {
        Authentication authentication = getAuthentication("USER");

        urlService.deleteById(geUrlIdByUsername("testuser1"), authentication);
        assertEquals(3, urlService.getAll().size());
    }

    @Test
    void deleteByIdThrowIllegalArgumentExceptionTest() {
        Authentication authentication = getAuthentication("ADMIN");
        assertThrows(IllegalArgumentException.class,
                () -> urlService.deleteById(1000L, authentication));
    }

    @Test
    void deleteByIdThrowAccessDeniedExceptionTest() {
        Authentication authentication = getAuthentication("USER");
        assertThrows(AccessDeniedException.class,
                () -> urlService.deleteById(geUrlIdByUsername("testadmin"), authentication));
    }

    @Test
    void updateUrlTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google.com", "valid url");
        Authentication authentication = getAuthentication("USER");

        urlService.updateUrl(geUrlIdByUsername("testuser1"), updateUrlRequest, authentication);
        assertAll(
                () -> assertEquals("google", urlService.getById(geUrlIdByUsername("testuser1")).getShortUrl()),
                () -> assertEquals(4, urlService.getAll().size())
        );
    }

    @Test
    void updateUrlThrowIllegalArgumentExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google.com", "valid url");
        Authentication authentication = getAuthentication("USER");

        assertThrows(IllegalArgumentException.class,
                () -> urlService.updateUrl(1000L, updateUrlRequest, authentication));
    }

    @Test
    void updateUrlThrowAccessDeniedExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google.com", "valid url");
        Authentication authentication = getAuthentication("USER");

        assertThrows(AccessDeniedException.class,
                () -> urlService.updateUrl(geUrlIdByUsername("testadmin"), updateUrlRequest, authentication));
    }

    @Test
    void updateUrlThrowAlreadyExistUrlExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("testurl1",
                "http://google.com", "incorrect short url");
        Authentication authentication = getAuthentication("USER");

        assertThrows(AlreadyExistUrlException.class,
                () -> urlService.updateUrl(geUrlIdByUsername("testuser1"), updateUrlRequest, authentication));
    }

    @Test
    void updateUrlThrowNotAccessibleExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google111.com", "incorrect url");
        Authentication authentication = getAuthentication("USER");

        assertThrows(NotAccessibleException.class,
                () -> urlService.updateUrl(geUrlIdByUsername("testuser1"), updateUrlRequest, authentication));
    }

    @Test
    void getByIdTest() {
        Long idForTestUrl3 = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getShortUrl().equals("testurl3"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();
        assertEquals("for test only3", urlService.getById(idForTestUrl3).getDescription());
    }

    @Test
    void getAllUrlUserTest() {
        Authentication authentication = getAuthentication("ADMIN");
        assertEquals(2, urlService.getAllUrlUser(authentication).size());
    }

    @Test
    void getUserActiveUrlTest() {
        Authentication authentication = getAuthentication("ADMIN");
        assertEquals(2, urlService.getUserActiveUrl(authentication).size());
    }

    @Test
    void getUserInactiveUrTest() {
        Authentication authentication = getAuthentication("ADMIN");
        assertEquals(0, urlService.getUserInactiveUrl(authentication).size());
    }

    @Test
    void getActiveUrlTest() {
        assertEquals(4, urlService.getActiveUrl().size());
    }

    @Test
    void getInactiveUrlTest() {
        assertEquals(0, urlService.getInactiveUrl().size());
    }

    @Test
    void prolongTest() {
        Authentication authentication = getAuthentication("USER");

        UrlDto urlToProlong = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testuser1"))
                .findFirst()
                .orElseThrow();

        Long urlIdToProlong = urlToProlong.getId();
        LocalDate oldExpirationDate = urlToProlong.getExpirationDate();

        urlService.prolong(urlIdToProlong, authentication);
        assertEquals(oldExpirationDate.plusDays(UrlEntity.VALID_DAYS), urlService.getById(urlIdToProlong).getExpirationDate());
    }

    @Test
    void prolongThrowIllegalArgumentExceptionTest() {
        Authentication authentication = getAuthentication("USER");

        assertThrows(IllegalArgumentException.class,
                () -> urlService.prolong(1000L, authentication));
    }

    @Test
    void prolongThrowAccessDeniedExceptionTest() {
        Authentication authentication = getAuthentication("USER");

        assertThrows(AccessDeniedException.class,
                () -> urlService.prolong(geUrlIdByUsername("testadmin"), authentication));
    }

    @Test
    void redirectToUrlTest() throws IOException {
        urlService.redirectToUrl("testurl1", response);

        Long urlIdRedirect = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getShortUrl().equals("testurl1"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        assertEquals(2, urlService.getById(urlIdRedirect).getVisitCount());
    }

    @Test
    void redirectToUrlThrowIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class,
                () -> urlService.redirectToUrl("WrongUrl", response));
    }

    private Long geUrlIdByUsername(String username) {
        return urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals(username))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();
    }

    private Authentication getAuthentication(String role) {
        if (role.equals("USER")) {
            return new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                    Collections.singleton(new SimpleGrantedAuthority("USER")));
        } else {
            return new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                    Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        }
    }
}