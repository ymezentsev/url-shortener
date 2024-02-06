package ua.goit.urlshortener.mvc.admin;

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
@WithMockUser(username = "testadmin", password = "qwerTy12", authorities = "ADMIN")
class AdminUrlsWebControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Find all urls")
    void getAllLinksTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_URLS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS,
                                ATTRIBUTE_USER_URLS, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Find all active urls")
    void getAllActiveLinksTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls/active"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_URLS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS,
                                ATTRIBUTE_USER_URLS)
                );
    }

    @Test
    @DisplayName("Find all inactive urls")
    void getAllInactiveLinksTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls/inactive"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_URLS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS,
                                ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Delete url")
    void deleteUrlTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls/delete/4"))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/V2/admin/urls")
                );
    }

    @Test
    @DisplayName("Prolong expiration date of url")
    void prolongUrlTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls/prolongation/3"))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/V2/admin/urls")
                );
    }

    @Test
    @DisplayName("Open a page for update url")
    void editUrlTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls/edit/2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_EDIT),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_URLS,
                                ATTRIBUTE_ID, ATTRIBUTE_FROM_ADMIN_PAGE),
                        model().attribute(ATTRIBUTE_FROM_ADMIN_PAGE, "true")
                );
    }

    @Test
    @DisplayName("Successful update url")
    void postEditUrlWithCorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/admin/urls/edit")
                        .contentType("application/json")
                        .param("id", "2")
                        .param("shortUrl", "newShortUrl")
                        .param("url", "google.com")
                        .param("description", "google")
                        .param("fromAdminPage", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_URLS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USER_URLS,
                                ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }

    @Test
    @DisplayName("Fail update url")
    void postEditUrlWithIncorrectDataTest() throws Exception {
        mockMvc.perform(post("/V2/admin/urls/edit")
                        .contentType("application/json")
                        .param("id", "2")
                        .param("shortUrl", "")
                        .param("url", "")
                        .param("description", "google")
                        .param("fromAdminPage", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_EDIT),
                        model().attributeExists(ATTRIBUTE_ERRORS, ATTRIBUTE_USERNAME, ATTRIBUTE_URLS,
                                ATTRIBUTE_ID, ATTRIBUTE_FROM_ADMIN_PAGE)
                );
    }

    @Test
    @DisplayName("Find all urls for selected user")
    void allSelectedUserUrlsTest() throws Exception {
        mockMvc.perform(get("/V2/admin/urls/2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_URLS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS,
                                ATTRIBUTE_USER_URLS, ATTRIBUTE_USER_URLS_INACTIVE)
                );
    }
}