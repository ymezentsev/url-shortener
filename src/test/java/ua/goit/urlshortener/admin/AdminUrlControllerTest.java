package ua.goit.urlshortener.admin;

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
import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserService;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminUrlControllerTest {
    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private static final ThreadLocal<String> token = new ThreadLocal<>();

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        loginAdmin();
        RestAssured.baseURI = "http://localhost:" + port + "/V1/admin/urls";
    }

    @Test
    void getUrlsForSelectedUserTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("userId", 2L)
                .when().get("/{userId}")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void getActiveUsersUrlsTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("userId", 2L)
                .when().get("/{userId}/active")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void getInactiveUsersUrlsTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("userId", 2L)
                .when().get("/{userId}/inactive")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(0));
    }

    private void loginAdmin() {
        if (Objects.isNull(token.get())) {
            CreateUserRequest request = new CreateUserRequest("testadmin", "qwerTy12");
            token.set(userService.loginUser(request));
        }
    }
}