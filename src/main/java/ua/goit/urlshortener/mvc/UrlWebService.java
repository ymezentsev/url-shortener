package ua.goit.urlshortener.mvc;

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

        ModelAndView result = new ModelAndView("edit");
        result.addObject("errors", errors);
        result.addObject("username", authentication.getName());
        result.addObject("urls", updateUrlRequest);
        result.addObject("id", id);
        result.addObject("fromAdminPage", fromAdminPage);
        return result;
    }

    public ModelAndView updateUrl(Long id, UpdateUrlRequest updateUrlRequest, Boolean fromAdminPage, Authentication authentication) {
        try {
            urlService.updateUrl(id, updateUrlRequest, authentication);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView("edit");
            result.addObject("errors", e.getMessage());
            result.addObject("username", authentication.getName());
            result.addObject("urls", updateUrlRequest);
            result.addObject("id", id);
            result.addObject("fromAdminPage", fromAdminPage);
            return result;
        }

        ModelAndView result = new ModelAndView("all-user");
        if (fromAdminPage) {
            result = new ModelAndView("admin-urls");
            result.addObject("username", authentication.getName());
            result.addObject("userUrls", urlService.getActiveUrl());
            result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        } else {
            result.addObject("username", authentication.getName());
            result.addObject("userUrls", urlService.getActiveUrlUser(authentication));
            result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(authentication));
        }
        return result;
    }

    public ModelAndView getCreateModelAndViewWithErrors(BindingResult bindingResult,
                                                        Authentication authentication) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ModelAndView result = new ModelAndView("create");
        result.addObject("errors", errors);
        result.addObject("username", authentication.getName());
        return result;
    }

    public ModelAndView createUrl(CreateUrlRequest createUrlRequest, Authentication authentication) {
        try {
            urlService.createUrl(createUrlRequest, authentication);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView("create");
            result.addObject("errors", e.getMessage());
            result.addObject("username", authentication.getName());
            return result;
        }
        ModelAndView result = new ModelAndView("all-user");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrlUser(authentication));
        result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(authentication));
        return result;
    }
}
