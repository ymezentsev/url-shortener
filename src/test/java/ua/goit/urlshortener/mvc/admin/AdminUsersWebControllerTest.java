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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@WithMockUser(username = "testadmin", password = "qwerTy12", authorities = "ADMIN")
class AdminUsersWebControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Find all users")
    void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/V2/admin/users"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_USERS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS)
                );
    }

    @Test
    @DisplayName("Delete user")
    void deleteUserTest() throws Exception {
        mockMvc.perform(get("/V2/admin/users/delete/{id}", 2))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_USERS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS)
                );
    }

    @Test
    @DisplayName("Change user's role")
    void changeUserRoleTest() throws Exception {
        mockMvc.perform(get("/V2/admin/users/edit/{id}", 3))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("text/html;charset=UTF-8"),
                        view().name(MODEL_ADMIN_USERS),
                        model().attributeExists(ATTRIBUTE_USERNAME, ATTRIBUTE_USERS)
                );
    }
}