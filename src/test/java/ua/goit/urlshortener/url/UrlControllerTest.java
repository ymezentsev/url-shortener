package ua.goit.urlshortener.url;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserRepository;
import ua.goit.urlshortener.user.UserService;

import java.time.LocalDate;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlControllerTest {
    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private static final ThreadLocal<String> token = new ThreadLocal<>();

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        cleanAndPopulateDb();
        RestAssured.baseURI = "http://localhost:" + port + "/V1/urls";
    }

    @Test
    void getAllUrlTest() {
        given().contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }

    @Test
    void createUrlTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://google.com", "valid url");

        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .body(createUrlRequest)
                .when().post("/create")
                .then()
                .statusCode(200)
                .assertThat()
                .body("shortUrl", notNullValue());
    }

    @Test
    void createUrlWithIncorrectDataTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("", "incorrect url");

        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .body(createUrlRequest)
                .when().post("/create")
                .then()
                .statusCode(400)
                .assertThat()
                .body(containsString("Url is required"));
    }

    @Test
    void getAllUserUrlsTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get("/current")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(2));
    }

    @Test
    void updateUrlTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("google", "http://google.com", "valid url");

        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUrlId())
                .body(updateUrlRequest)
                .when().post("/edit/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    void updateUrlWithIncorrectDataTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("", "http://google.com", "incorrect short url");

        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUrlId())
                .body(updateUrlRequest)
                .when().post("/edit/{id}")
                .then()
                .statusCode(400)
                .assertThat()
                .body(containsString("Short url is required"));
    }

    @Test
    void deleteUrlTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }

        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUrlId())
                .when().delete("/delete/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    void prolongUrlTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }

        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUrlId())
                .when().post("/prolongation/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    void getUserActiveUrlsTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get("/current/active")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(2));
    }

    @Test
    void getUserInactiveUrlsTest() {
        if (Objects.isNull(token.get())) {
            loginUser();
        }
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get("/current/inactive")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(0));
    }

    @Test
    void getActiveUrlsTest() {
        given().contentType(ContentType.JSON)
                .when().get("/active")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }

    @Test
    void getInactiveUrlsTest() {
        given().contentType(ContentType.JSON)
                .when().get("/inactive")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(0));
    }

    @Test
    void redirectToUrlTest() {
        given().contentType(ContentType.JSON)
                .pathParam("shortUrl", "testurl1")
                .when().get("/{shortUrl}")
                .then()
                .statusCode(200)
                .assertThat()
                .body(containsString("Google"));
    }

    private void cleanAndPopulateDb() {
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

    private void loginUser() {
        CreateUserRequest request = new CreateUserRequest("testadmin", "qwerTy12");
        token.set(userService.loginUser(request));
    }

    private Long getUrlId(){
        return urlRepository.findByUserId(1L).stream()
                .map(UrlEntity::getId)
                .findFirst()
                .orElseThrow();
    }
}