package ua.goit.urlshortener.admin;

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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.goit.urlshortener.DBInitializer;
import ua.goit.urlshortener.user.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Autowired
    DBInitializer dbInitializer;

    @BeforeEach
    void setUp() {
        setAdminAuthentication();
        dbInitializer.cleanAndPopulateDb();
    }

    @Test
    @DisplayName("Find all users")
    void getAllUsersTest() {
        assertEquals(3, adminService.getAllUsers().size());
    }

    @Test
    @DisplayName("Successful delete user")
    void deleteUserByIdTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userIdToDelete = getUserIdByUsername("testuser1");

        adminService.deleteUserById(userIdToDelete, authentication);
        assertEquals(2, adminService.getAllUsers().size());
    }

    @Test
    @DisplayName("Fail delete user (user not exists)")
    void deleteUserByIdThrowIllegalArgumentExceptionTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUserById(1000L, authentication));
    }

    @Test
    @DisplayName("Fail delete user (user can't delete yourself)")
    void deleteUserByIdThrowIllegalArgumentExceptionWhenDeleteYourselfTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userIdToDelete = getUserIdByUsername("testadmin");

        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUserById(userIdToDelete, authentication));
    }

    @Test
    @DisplayName("Successful change user's role")
    void changeUserRoleTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userIdToChangeRole = getUserIdByUsername("testuser1");

        adminService.changeUserRole(userIdToChangeRole, authentication);
        assertEquals("ADMIN", userService.findByUsername("testuser1").getRole().name());
    }

    @Test
    @DisplayName("Fail change user's role (user not exists)")
    void changeUserRoleThrowIllegalArgumentExceptionTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThrows(IllegalArgumentException.class, () -> adminService.changeUserRole(1000L, authentication));
    }

    @Test
    @DisplayName("Fail change user's role (user can't change role for yourself)")
    void changeUserRoleThrowIllegalArgumentExceptionWhenDeleteYourselfTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userIdToDelete = getUserIdByUsername("testadmin");

        assertThrows(IllegalArgumentException.class, () -> adminService.changeUserRole(userIdToDelete, authentication));
    }

    @Test
    @DisplayName("Find all ulrs for selected user")
    void getUrlsForSelectedUserTest() {
        assertAll(
                () -> assertEquals(2,
                        adminService.getUrlsForSelectedUser(getUserIdByUsername("testadmin")).size()),
                () -> assertEquals(1,
                        adminService.getUrlsForSelectedUser(getUserIdByUsername("testuser1")).size())
        );
    }

    @Test
    @DisplayName("Find all active ulrs for selected user")
    void getActiveUrlsForSelectedUserTest() {
        assertAll(
                () -> assertEquals(2,
                        adminService.getActiveUrlsForSelectedUser(getUserIdByUsername("testadmin")).size()),
                () -> assertEquals(1,
                        adminService.getActiveUrlsForSelectedUser(getUserIdByUsername("testuser1")).size())
        );
    }

    @Test
    @DisplayName("Find all inactive ulrs for selected user")
    void getInactiveUrlsForSelectedUserTest() {
        assertAll(
                () -> assertEquals(0,
                        adminService.getInactiveUrlsForSelectedUser(getUserIdByUsername("testadmin")).size()),
                () -> assertEquals(0,
                        adminService.getInactiveUrlsForSelectedUser(getUserIdByUsername("testuser1")).size())
        );
    }

    private void setAdminAuthentication() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testadmin", "qwerTy12",
                        Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Long getUserIdByUsername(String username) {
        return userService.findByUsername(username).getId();
    }
}