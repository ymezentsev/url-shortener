package ua.goit.urlshortener.mvc.url;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

import java.util.List;

import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@Service
@RequiredArgsConstructor
public class UrlWebService {
    private final UrlService urlService;

    public ModelAndView getEditModelAndViewWithErrors(BindingResult bindingResult,
                                                      UpdateUrlRequest updateUrlRequest,
                                                      Long id,
                                                      Boolean fromAdminPage,
                                                      Authentication authentication) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ModelAndView result = new ModelAndView(MODEL_EDIT);
        result.addObject(ATTRIBUTE_ERRORS, errors);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_URLS, updateUrlRequest);
        result.addObject(ATTRIBUTE_ID, id);
        result.addObject(ATTRIBUTE_FROM_ADMIN_PAGE, fromAdminPage);
        return result;
    }

    public ModelAndView updateUrl(Long id, UpdateUrlRequest updateUrlRequest, Boolean fromAdminPage, Authentication authentication) {
        try {
            urlService.updateUrl(id, updateUrlRequest, authentication);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView(MODEL_EDIT);
            result.addObject(ATTRIBUTE_ERRORS, e.getMessage());
            result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
            result.addObject(ATTRIBUTE_URLS, updateUrlRequest);
            result.addObject(ATTRIBUTE_ID, id);
            result.addObject(ATTRIBUTE_FROM_ADMIN_PAGE, fromAdminPage);
            return result;
        }

        ModelAndView result = new ModelAndView(MODEL_ALL_USER);
        if (Boolean.TRUE.equals(fromAdminPage)) {
            result = new ModelAndView(MODEL_ADMIN_URLS);
            result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
            result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrl());
            result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrl());
        } else {
            result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
            result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrlUser(authentication));
            result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrlUser(authentication));
        }
        return result;
    }

    public ModelAndView getCreateModelAndViewWithErrors(BindingResult bindingResult,
                                                        Authentication authentication) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ModelAndView result = new ModelAndView(MODEL_CREATE);
        result.addObject(ATTRIBUTE_ERRORS, errors);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        return result;
    }

    public ModelAndView createUrl(CreateUrlRequest createUrlRequest, Authentication authentication) {
        try {
            urlService.createUrl(createUrlRequest, authentication);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView(MODEL_CREATE);
            result.addObject(ATTRIBUTE_ERRORS, e.getMessage());
            result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
            return result;
        }
        ModelAndView result = new ModelAndView(MODEL_ALL_USER);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrlUser(authentication));
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrlUser(authentication));
        return result;
    }
}
