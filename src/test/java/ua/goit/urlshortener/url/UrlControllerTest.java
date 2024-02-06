package ua.goit.urlshortener.url;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.goit.urlshortener.DBInitializer;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserService;

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
    UserService userService;

    @Autowired
    DBInitializer dbInitializer;

    @BeforeEach
    void setUp() {
        dbInitializer.cleanAndPopulateUrlTable();
        RestAssured.baseURI = "http://localhost:" + port + "/V1/urls";
    }

    @Test
    @DisplayName("Find all urls")
    void getAllUrlTest() {
        given().contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }

    @Test
    @DisplayName("Successful create new url")
    void createUrlTest() {
        loginUser();
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
    @DisplayName("Fail create new url")
    void createUrlWithIncorrectDataTest() {
        loginUser();
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
    @DisplayName("Find all urls for current user")
    void getAllUserUrlsTest() {
        loginUser();
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get("/current")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(2));
    }

    @Test
    @DisplayName("Successful update url")
    void updateUrlTest() {
        loginUser();
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
    @DisplayName("Fail update url")
    void updateUrlWithIncorrectDataTest() {
        loginUser();
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
    @DisplayName("Successful delete url")
    void deleteUrlTest() {
        loginUser();
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUrlId())
                .when().delete("/delete/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Prolongation url's expired date")
    void prolongUrlTest() {
        loginUser();
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUrlId())
                .when().post("/prolongation/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Find all active urls for current user")
    void getUserActiveUrlsTest() {
        loginUser();
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get("/current/active")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(2));
    }

    @Test
    @DisplayName("Find all inactive urls for current user")
    void getUserInactiveUrlsTest() {
        loginUser();
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get("/current/inactive")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(0));
    }

    @Test
    @DisplayName("Find all active urls")
    void getActiveUrlsTest() {
        given().contentType(ContentType.JSON)
                .when().get("/active")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }

    @Test
    @DisplayName("Find all inactive urls")
    void getInactiveUrlsTest() {
        given().contentType(ContentType.JSON)
                .when().get("/inactive")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(0));
    }

    @Test
    @DisplayName("Redirect to url")
    void redirectToUrlTest() {
        given().contentType(ContentType.JSON)
                .pathParam("shortUrl", "testurl1")
                .when().get("/{shortUrl}")
                .then()
                .statusCode(200)
                .assertThat()
                .body(containsString("Google"));
    }

    private void loginUser() {
        if (Objects.isNull(token.get())) {
            CreateUserRequest request = new CreateUserRequest("testadmin", "qwerTy12");
            token.set(userService.loginUser(request));
        }
    }

    private Long getUrlId() {
        return urlRepository.findByUserId(1L).stream()
                .map(UrlEntity::getId)
                .findFirst()
                .orElseThrow();
    }
}