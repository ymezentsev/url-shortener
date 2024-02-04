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
import ua.goit.urlshortener.url.UrlDto;
import ua.goit.urlshortener.url.UrlEntity;
import ua.goit.urlshortener.url.UrlRepository;
import ua.goit.urlshortener.url.exceptions.AlreadyExistUrlException;
import ua.goit.urlshortener.url.exceptions.NotAccessibleException;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.user.UserRepository;

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
    UrlRepository urlRepository;
    @Autowired
    UserRepository userRepository;

    @Mock
    HttpServletResponse response;

    @BeforeEach
    void cleanAndPopulateDb() {
        urlRepository.deleteAll();

        urlRepository.save(new UrlEntity(null, "testurl1", "https://google.com/",
                "for test only1", userRepository.findById(1L).orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));

        urlRepository.save(new UrlEntity(null, "testurl2", "https://some_long_named_portal.com/",
                "for test only2", userRepository.findById(1L).orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));

        urlRepository.save(new UrlEntity(null, "testurl3", "https://some_long_named_portal.com/",
                "for test only3", userRepository.findById(2L).orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));

        urlRepository.save(new UrlEntity(null, "testurl4", "https://some_long_named_portal.com/",
                "for test only4", userRepository.findById(3L).orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));
    }

    @Test
    void getAllTest() {
        assertEquals(4, urlService.getAll().size());
    }

    @Test
    void createUrlTest() {
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://google.com", "valid url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));

        assertAll(
                () -> assertNotNull(urlService.createUrl(createUrlRequest, authentication).getShortUrl()),
                () -> assertEquals(5, urlService.getAll().size())
        );
    }

    @Test
    void createUrlThrowExceptionTest() {
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://wrongUrl.com", "incorrect url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));

        assertThrows(NotAccessibleException.class,
                () -> urlService.createUrl(createUrlRequest, authentication));
    }

    @Test
    void deleteByIdTest() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToDelete = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testuser1"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        urlService.deleteById(urlIdToDelete, authentication);
        assertEquals(3, urlService.getAll().size());
    }

    @Test
    void deleteByIdThrowIllegalArgumentExceptionTest() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));

        assertThrows(IllegalArgumentException.class,
                () -> urlService.deleteById(1000L, authentication));
    }

    @Test
    void deleteByIdThrowAccessDeniedExceptionTest() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToDelete = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testadmin"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        assertThrows(AccessDeniedException.class,
                () -> urlService.deleteById(urlIdToDelete, authentication));
    }

    @Test
    void updateUrlTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google.com", "valid url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToUpdate = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testuser1"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        urlService.updateUrl(urlIdToUpdate, updateUrlRequest, authentication);
        assertAll(
                () -> assertEquals("google", urlService.getById(urlIdToUpdate).getShortUrl()),
                () -> assertEquals(4, urlService.getAll().size())
        );
    }

    @Test
    void updateUrlThrowIllegalArgumentExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google.com", "valid url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        assertThrows(IllegalArgumentException.class,
                () -> urlService.updateUrl(1000L, updateUrlRequest, authentication));
    }

    @Test
    void updateUrlThrowAccessDeniedExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google.com", "valid url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToUpdate = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testadmin"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        assertThrows(AccessDeniedException.class,
                () -> urlService.updateUrl(urlIdToUpdate, updateUrlRequest, authentication));
    }

    @Test
    void updateUrlThrowAlreadyExistUrlExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("testurl1",
                "http://google.com", "incorrect short url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToUpdate = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testuser1"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        assertThrows(AlreadyExistUrlException.class,
                () -> urlService.updateUrl(urlIdToUpdate, updateUrlRequest, authentication));
    }

    @Test
    void updateUrlThrowNotAccessibleExceptionTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google",
                "http://google111.com", "incorrect url");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToUpdate = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testuser1"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        assertThrows(NotAccessibleException.class,
                () -> urlService.updateUrl(urlIdToUpdate, updateUrlRequest, authentication));
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
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));

        assertEquals(2, urlService.getAllUrlUser(authentication).size());
    }

    @Test
    void getUserActiveUrlTest() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));

        assertEquals(2, urlService.getUserActiveUrl(authentication).size());
    }

    @Test
    void getUserInactiveUrTest() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));

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
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

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
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        assertThrows(IllegalArgumentException.class,
                () -> urlService.prolong(1000L, authentication));
    }

    @Test
    void prolongThrowAccessDeniedExceptionTest() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("USER")));

        Long urlIdToProlong = urlService.getAll().stream()
                .filter(urlDto -> urlDto.getUsername().equals("testadmin"))
                .map(UrlDto::getId)
                .findFirst()
                .orElseThrow();

        assertThrows(AccessDeniedException.class,
                () -> urlService.prolong(urlIdToProlong, authentication));
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
}