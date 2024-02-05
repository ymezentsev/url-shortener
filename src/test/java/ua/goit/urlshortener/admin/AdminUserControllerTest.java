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
import ua.goit.urlshortener.DBInitializer;
import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserService;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminUserControllerTest {
    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private static final ThreadLocal<String> token = new ThreadLocal<>();

    @Autowired
    UserService userService;

    @Autowired
    DBInitializer dbInitializer;

    @BeforeEach
    void setUp() {
        loginAdmin();
        dbInitializer.cleanAndPopulateDb();
        RestAssured.baseURI = "http://localhost:" + port + "/V1/admin/users";
    }

    @Test
    void getAllUsersTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .when().get()
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(3));
    }

    @Test
    void changeUserRoleTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUserIdByUsername("testuser1"))
                .when().post("/edit/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    void deleteUserTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token.get())
                .pathParam("id", getUserIdByUsername("testuser1"))
                .when().delete("/delete/{id}")
                .then()
                .statusCode(200);
    }

    private void loginAdmin() {
        if (Objects.isNull(token.get())) {
            CreateUserRequest request = new CreateUserRequest("testadmin", "qwerTy12");
            token.set(userService.loginUser(request));
        }
    }

    private Long getUserIdByUsername(String username) {
        return userService.findByUsername(username).getId();
    }
}