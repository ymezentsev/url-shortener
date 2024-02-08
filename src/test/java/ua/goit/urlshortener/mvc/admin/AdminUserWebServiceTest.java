package ua.goit.urlshortener.mvc.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.goit.urlshortener.mvc.ConstantsStorage.ATTRIBUTE_ERRORS;
import static ua.goit.urlshortener.mvc.ConstantsStorage.ATTRIBUTE_USERS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminUserWebServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    AdminUserWebService adminUserWebService;

    @BeforeEach
    void setUp() {
        setAdminAuthentication();
    }

    @Test
    @DisplayName("Successful delete user")
    void deleteUserWithCorrectDataTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView deleteUserWebModel = adminUserWebService.deleteUser(2L, authentication);
        assertTrue(deleteUserWebModel.getModel().containsKey(ATTRIBUTE_USERS));
    }

    @Test
    @DisplayName("Fail delete user")
    void deleteUserWithIncorrectDataTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView deleteUserWebModel = adminUserWebService.deleteUser(2L, authentication);
        assertTrue(deleteUserWebModel.getModel().containsKey(ATTRIBUTE_ERRORS));
    }

    @Test
    @DisplayName("Successful change user's role")
    void changeUserRoleWithCorrectDataTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView changeUserRoleWebModel = adminUserWebService.changeUserRole(3L, authentication);
        assertTrue(changeUserRoleWebModel.getModel().containsKey(ATTRIBUTE_USERS));
    }

    @Test
    @DisplayName("Fail change user's role")
    void changeUserRoleWithIncorrectDataTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView changeUserRoleWebModel = adminUserWebService.changeUserRole(2L, authentication);
        assertTrue(changeUserRoleWebModel.getModel().containsKey(ATTRIBUTE_ERRORS));
    }

    private void setAdminAuthentication() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}