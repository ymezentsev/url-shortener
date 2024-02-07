package ua.goit.urlshortener.mvc.url;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
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
class UrlWebControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Open an index page for anonymous user")
    void getIndexPageTest() throws Exception {
        mockMvc.perform(get("/V2/urls"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_INDEX)
                );
    }

    @Test
    @DisplayName("Open an index page for authenticated user")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void getIndexPageForUserTest() throws Exception {
        mockMvc.perform(get("/V2/urls/user"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_INDEX),
                        model().attributeExists(ATTRIBUTE_USERNAME)
                );
    }

    @Test
    @DisplayName("Find all urls")
    void getAllLinksTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_GUEST),
                        model().attributeExists(ATTRIBUTE_USER_URLS, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Redirect to url")
    void redirectToUrlTest() throws Exception {
        mockMvc.perform(get("/V2/urls/{shortUrl}", "testurl1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Find all active urls")
    void getAllActiveLinksTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list/active"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_GUEST),
                        model().attributeExists(ATTRIBUTE_USER_URLS)
                );
    }

    @Test
    @DisplayName("Find all inactive urls")
    void getAllInactiveLinksTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list/inactive"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_GUEST),
                        model().attributeExists(ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Open a page for work with urls for authenticated user")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void getAllLinksAuthTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list/auth"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_GUEST),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Find all urls for current user")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void getAllUsersLinksTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list/user"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_USER),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Find all active urls for current user")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void getAllUsersActiveLinksTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list/user/active"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_USER),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS)
                );
    }

    @Test
    @DisplayName("Find all inactive urls for current user")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void getAllUsersInactiveLinksTest() throws Exception {
        mockMvc.perform(get("/V2/urls/list/user/inactive"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_USER),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Open a page for update url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void editUrlTest() throws Exception {
        mockMvc.perform(get("/V2/urls/edit/{id}", 3))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_EDIT),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_URLS, ATTRIBUTE_ID)
                );
    }

    @Test
    @DisplayName("Successful update url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void postEditUrlWithCorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/urls/edit")
                        .contentType("application/json")
                        .param("id", "3")
                        .param("shortUrl", "newShortUrl")
                        .param("url", "google.com")
                        .param("description", "google"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_USER),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS,
                                ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Fail update url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void postEditUrlWithIncorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/urls/edit")
                        .contentType("application/json")
                        .param("id", "3")
                        .param("shortUrl", "")
                        .param("url", "")
                        .param("description", "google"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_EDIT),
                        model().attributeExists(ATTRIBUTE_ERRORS, ATTRIBUTE_USERNAME,
                                ATTRIBUTE_URLS, ATTRIBUTE_ID)
                );
    }

    @Test
    @DisplayName("Delete url")
    @WithMockUser(username = "testuser2", password = "qwerTy12", authorities = "USER")
    void deleteUrlTest() throws Exception {
        mockMvc.perform(get("/V2/urls/delete/{id}", 4))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/V2/urls/list/user")
                );
    }

    @Test
    @DisplayName("Prolong expiration date of url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void prolongUrlTest() throws Exception {
        mockMvc.perform(get("/V2/urls/prolongation/{id}", 3))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/V2/urls/list/user")
                );
    }

    @Test
    @DisplayName("Open a page for create url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void createUrlTest() throws Exception {
        mockMvc.perform(get("/V2/urls/create"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_CREATE),
                        model().attributeExists(ATTRIBUTE_USERNAME)
                );
    }

    @Test
    @DisplayName("Successful create url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void postCreateUrlWithCorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/urls/create")
                        .contentType("application/json")
                        .param("url", "google.com")
                        .param("description", "google"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ALL_USER),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS,
                                ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Fail create url")
    @WithMockUser(username = "testuser1", password = "qwerTy12", authorities = "USER")
    void postCreateUrlWithIncorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/urls/create")
                        .contentType("application/json")
                        .param("url", "")
                        .param("description", "google"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_CREATE),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_ERRORS)
                );
    }
}