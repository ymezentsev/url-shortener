package ua.goit.urlshortener.mvc.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.goit.urlshortener.user.CreateUserRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserWebServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserWebService userWebService;

    @Mock
    BindingResult bindingResult;

    @Test
    void registerUserWithCorrectDataTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("test", "qwerTy12");

        ModelAndView createUserWebModel = userWebService.registerUser(createUserRequest);
        assertEquals(MODEL_SUCCESS, createUserWebModel.getViewName());
    }

    @Test
    void registerUserWithIncorrectDataTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testadmin", "qwerTy12");

        ModelAndView createUserWebModel = userWebService.registerUser(createUserRequest);
        assertAll(
                () -> assertEquals(MODEL_REGISTER, createUserWebModel.getViewName()),
                () -> assertTrue(createUserWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void loginUserWithCorrectDataTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testuser1", "qwerTy12");

        ModelAndView loginUserWebModel = userWebService.loginUser(createUserRequest);
        assertAll(
                () -> assertEquals(MODEL_ALL_USER, loginUserWebModel.getViewName()),
                () -> assertTrue(loginUserWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertTrue(loginUserWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS_INACTIVE)),
                () -> assertFalse(loginUserWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void loginUserWithIncorrectDataTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest("user", "qwerTy12");

        ModelAndView loginUserWebModel = userWebService.loginUser(createUserRequest);
        assertAll(
                () -> assertEquals(MODEL_LOGIN, loginUserWebModel.getViewName()),
                () -> assertTrue(loginUserWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void getModelAndViewWithErrorsTest() {
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());
        ModelAndView result = new ModelAndView(MODEL_LOGIN);

        userWebService.getModelAndViewWithErrors(bindingResult, result);
        assertTrue(result.getModel().containsKey(ATTRIBUTE_ERRORS));
    }
}