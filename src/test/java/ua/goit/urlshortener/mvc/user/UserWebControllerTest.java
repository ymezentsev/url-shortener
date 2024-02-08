package ua.goit.urlshortener.mvc.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.goit.urlshortener.mvc.ConstantsStorage.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class UserWebControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Open a page for registration")
    void getRegisterUserTest() throws Exception {
        mockMvc.perform(get("/V2/users/register"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_REGISTER)
                );
    }

    @Test
    @DisplayName("Successful registration")
    void postRegisterUserWithCorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/users/register")
                        .contentType("application/json")
                        .param("register", "true")
                        .param("username", "user")
                        .param("password", "Password123"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_SUCCESS)
                );
    }

    @Test
    @DisplayName("Fail registration")
    void postRegisterUserWithIncorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/users/register")
                        .contentType("application/json")
                        .param("register", "true")
                        .param("username", "")
                        .param("password", "Password"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_REGISTER),
                        model().attributeExists(ATTRIBUTE_ERRORS)
                );
    }

    @Test
    @DisplayName("Open a page for login")
    void getLoginUserTest() throws Exception {
        mockMvc.perform(get("/V2/users/login"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_LOGIN)
                );
    }

    @Test
    @DisplayName("Successful login")
    void postLoginUserWithCorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/users/login")
                        .contentType("application/json")
                        .param("username", "testuser1")
                        .param("password", "qwerTy12"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_USER),
                        model().attributeExists(ATTRIBUTE_USER_URLS, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Fail login")
    void postLoginUserWithIncorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/users/login")
                        .contentType("application/json")
                        .param("username", "")
                        .param("password", "Password"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_LOGIN),
                        model().attributeExists(ATTRIBUTE_ERRORS)
                );
    }
}