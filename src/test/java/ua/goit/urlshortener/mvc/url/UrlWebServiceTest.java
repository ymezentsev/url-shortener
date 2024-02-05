package ua.goit.urlshortener.mvc.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlWebServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UrlWebService urlWebService;

    @Mock
    BindingResult bindingResult;

    @Test
    void getUpdateModelAndViewWithErrorsTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("edited", "https://www.google.com/", "Edited");

        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());
        ModelAndView updateUrlWebModel = urlWebService.getUpdateModelAndViewWithErrors(bindingResult,
                updateUrlRequest, 1000L, false, getAuthentication());

        assertAll(
                () -> assertEquals(MODEL_EDIT, updateUrlWebModel.getViewName()),
                () -> assertFalse(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_ID)),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void updateUrlWithCorrectDataFromAdminPageTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("edited", "https://www.google.com/", "Edited");

        ModelAndView updateUrlWebModel = urlWebService.updateUrl(3L, updateUrlRequest,
                true, getAuthentication());
        assertAll(
                () -> assertEquals(MODEL_ADMIN_URLS, updateUrlWebModel.getViewName()),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS_INACTIVE)),
                () -> assertFalse(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void updateUrlWithCorrectDataFromUserPageTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("edited", "https://www.google.com/", "Edited");

        ModelAndView updateUrlWebModel = urlWebService.updateUrl(3L, updateUrlRequest,
                false, getAuthentication());
        assertAll(
                () -> assertEquals(MODEL_ALL_USER, updateUrlWebModel.getViewName()),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS_INACTIVE)),
                () -> assertFalse(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void updateUrlWithIncorrectDataTest() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest("edited", "https://www.google.com/", "Edited");

        ModelAndView updateUrlWebModel = urlWebService.updateUrl(1000L, updateUrlRequest,
                false, getAuthentication());
        assertAll(
                () -> assertEquals(MODEL_EDIT, updateUrlWebModel.getViewName()),
                () -> assertFalse(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertFalse(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS_INACTIVE)),
                () -> assertTrue(updateUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void getCreateModelAndViewWithErrorsTest() {
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());
        ModelAndView createUrlWebModel = urlWebService.getCreateModelAndViewWithErrors(bindingResult, getAuthentication());

        assertAll(
                () -> assertEquals(MODEL_CREATE, createUrlWebModel.getViewName()),
                () -> assertFalse(createUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertTrue(createUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void createUrlWithCorrectDataTest() {
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://google.com", "valid url");

        ModelAndView createUrlWebModel = urlWebService.createUrl(createUrlRequest, getAuthentication());
        assertAll(
                () -> assertEquals(MODEL_ALL_USER, createUrlWebModel.getViewName()),
                () -> assertTrue(createUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertTrue(createUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS_INACTIVE)),
                () -> assertFalse(createUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    @Test
    void createUrlWithIncorrectDataTest() {
        CreateUrlRequest createUrlRequest = new CreateUrlRequest("http://google1.com", "valid url");

        ModelAndView createUrlWebModel = urlWebService.createUrl(createUrlRequest, getAuthentication());
        assertAll(
                () -> assertEquals(MODEL_CREATE, createUrlWebModel.getViewName()),
                () -> assertFalse(createUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS)),
                () -> assertFalse(createUrlWebModel.getModel().containsKey(ATTRIBUTE_USER_URLS_INACTIVE)),
                () -> assertTrue(createUrlWebModel.getModel().containsKey(ATTRIBUTE_ERRORS))
        );
    }

    private Authentication getAuthentication() {
        return new UsernamePasswordAuthenticationToken("testuser1", "qwerTy12",
                Collections.singleton(new SimpleGrantedAuthority("USER")));
    }
}