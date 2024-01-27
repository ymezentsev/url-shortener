package ua.goit.urlshortener.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlWebService {
    private final UrlService urlService;

    public ModelAndView getEditModelAndViewWithErrors(BindingResult bindingResult,
                                                      UpdateUrlRequest updateUrlRequest,
                                                      Long id,
                                                      Boolean fromAdminPage,
                                                      Principal principal) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ModelAndView result = new ModelAndView("edit");
        result.addObject("errors", errors);
        result.addObject("username", principal.getName());
        result.addObject("urls", updateUrlRequest);
        result.addObject("id", id);
        result.addObject("fromAdminPage", fromAdminPage);
        return result;
    }

    public ModelAndView updateUrl(Principal principal, Long id, UpdateUrlRequest updateUrlRequest, Boolean fromAdminPage) {
        try {
            urlService.update(principal.getName(), id, updateUrlRequest);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView("edit");
            result.addObject("errors", e.getMessage());
            result.addObject("username", principal.getName());
            result.addObject("urls", updateUrlRequest);
            result.addObject("id", id);
            result.addObject("fromAdminPage", fromAdminPage);
            return result;
        }

        ModelAndView result = new ModelAndView("all-user");
        if (fromAdminPage) {
            result = new ModelAndView("admin-urls");
            result.addObject("username", principal.getName());
            result.addObject("userUrls", urlService.getActiveUrl());
            result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        } else {
            result.addObject("username", principal.getName());
            result.addObject("userUrls", urlService.getActiveUrlUser(principal.getName()));
            result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(principal.getName()));
        }
        return result;
    }

    public ModelAndView getCreateModelAndViewWithErrors(BindingResult bindingResult,
                                                        Principal principal) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ModelAndView result = new ModelAndView("create");
        result.addObject("errors", errors);
        result.addObject("username", principal.getName());
        return result;
    }

    public ModelAndView createUrl(Principal principal, CreateUrlRequest createUrlRequest) {
        try {
            urlService.createUrl(principal.getName(), createUrlRequest);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView("create");
            result.addObject("errors", e.getMessage());
            result.addObject("username", principal.getName());
            return result;
        }
        ModelAndView result = new ModelAndView("all-user");
        result.addObject("username", principal.getName());
        result.addObject("userUrls", urlService.getActiveUrlUser(principal.getName()));
        result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(principal.getName()));
        return result;
    }
}
