package ua.goit.urlshortener.mvc.url;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

import java.io.IOException;

import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@Controller
@RequestMapping("V2/urls")
@RequiredArgsConstructor
public class UrlWebController {
    private final UrlService urlService;
    private final UrlWebService urlWebService;

    @GetMapping()
    public String getIndexPage() {
        return MODEL_INDEX;
    }

    @GetMapping("/user")
    public ModelAndView getIndexPageForUser(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_INDEX);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        return result;
    }

    @GetMapping("/list")
    public ModelAndView getAllLinks() {
        ModelAndView result = new ModelAndView(MODEL_ALL_GUEST);
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrl());
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/{shortUrl}")
    public void redirectToUrl(@PathVariable("shortUrl") String shortUrl, HttpServletResponse response) throws IOException {
        urlService.redirectToUrl(shortUrl, response);
    }

    @GetMapping("/list/active")
    public ModelAndView getAllActiveLinks() {
        ModelAndView result = new ModelAndView(MODEL_ALL_GUEST);
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrl());
        return result;
    }

    @GetMapping("/list/inactive")
    public ModelAndView getAllInactiveLinks() {
        ModelAndView result = new ModelAndView(MODEL_ALL_GUEST);
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/list/auth")
    public ModelAndView getAllLinksAuth(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ALL_GUEST);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrl());
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/list/user")
    public ModelAndView getAllUsersLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ALL_USER);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getUserActiveUrl(authentication));
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getUserInactiveUrl(authentication));
        return result;
    }

    @GetMapping("/list/user/active")
    public ModelAndView getAllUsersActiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ALL_USER);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getUserActiveUrl(authentication));
        return result;
    }

    @GetMapping("/list/user/inactive")
    public ModelAndView getAllUsersInactiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ALL_USER);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getUserInactiveUrl(authentication));
        return result;
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView editUrl(@PathVariable("id") Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_EDIT);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_URLS, urlService.getById(id));
        result.addObject(ATTRIBUTE_ID, id);
        return result;
    }

    @PostMapping(value = "/edit")
    public ModelAndView postEditUrl(@Valid @ModelAttribute UpdateUrlRequest updateUrlRequest,
                                    BindingResult bindingResult,
                                    @RequestParam("id") Long id,
                                    @RequestParam(value = "fromAdminPage", defaultValue = "false") Boolean fromAdminPage,
                                    Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return urlWebService.getUpdateModelAndViewWithErrors(bindingResult, updateUrlRequest, id, fromAdminPage, authentication);
        }
        return urlWebService.updateUrl(id, updateUrlRequest, fromAdminPage, authentication);
    }

    @GetMapping(value = "/delete/{id}")
    public String deleteUrl(@PathVariable("id") Long id, Authentication authentication) {
        urlService.deleteById(id, authentication);
        return "redirect:/V2/urls/list/user";
    }

    @GetMapping(value = "/prolongation/{id}")
    public String prolongUrl(@PathVariable("id") Long id, Authentication authentication) {
        urlService.prolong(id, authentication);
        return "redirect:/V2/urls/list/user";
    }

    @GetMapping(value = "/create")
    public ModelAndView createUrl(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_CREATE);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        return result;
    }

    @PostMapping(value = "/create")
    public ModelAndView postCreateUrl(@Valid @ModelAttribute CreateUrlRequest createUrlRequest,
                                      BindingResult bindingResult,
                                      Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return urlWebService.getCreateModelAndViewWithErrors(bindingResult, authentication);
        }
        return urlWebService.createUrl(createUrlRequest, authentication);
    }
}
