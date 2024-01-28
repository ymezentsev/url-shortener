package ua.goit.urlshortener.mvc;

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

@Controller
@RequestMapping("V2/urls")
@RequiredArgsConstructor
public class UrlWebController {
    private final UrlService urlService;
    private final UrlWebService urlWebService;

    @GetMapping()
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/user")
    public ModelAndView getIndexPageForUser(Authentication authentication) {
        ModelAndView result = new ModelAndView("index");
        result.addObject("username", authentication.getName());
        return result;
    }

    @GetMapping("/list")
    public ModelAndView getAllLinks() {
        ModelAndView result = new ModelAndView("all-guest");
        result.addObject("userUrls", urlService.getActiveUrl());
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/{shortUrl}")
    public void redirectToUrl(@PathVariable("shortUrl") String shortUrl, HttpServletResponse response) throws IOException {
        urlService.redirectToUrl(shortUrl, response);
    }

    @GetMapping("/list/active")
    public ModelAndView getAllActiveLinks() {
        ModelAndView result = new ModelAndView("all-guest");
        result.addObject("userUrls", urlService.getActiveUrl());
        return result;
    }

    @GetMapping("/list/inactive")
    public ModelAndView getAllInactiveLinks() {
        ModelAndView result = new ModelAndView("all-guest");
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/list/auth")
    public ModelAndView getAllLinksAuth(Authentication authentication) {
        ModelAndView result = new ModelAndView("all-guest");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrl());
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/list/user")
    public ModelAndView getAllUsersLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView("all-user");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrlUser(authentication));
        result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(authentication));
        return result;
    }

    @GetMapping("/list/user/active")
    public ModelAndView getAllUsersActiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView("all-user");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrlUser(authentication));
        return result;
    }

    @GetMapping("/list/user/inactive")
    public ModelAndView getAllUsersInactiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView("all-user");
        result.addObject("username", authentication.getName());
        result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(authentication));
        return result;
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView("edit");
        result.addObject("username", authentication.getName());
        result.addObject("urls", urlService.getById(id));
        result.addObject("id", id);
        return result;
    }

    @PostMapping(value = "/edit")
    public ModelAndView postEdit(@Valid @ModelAttribute UpdateUrlRequest updateUrlRequest,
                                 BindingResult bindingResult,
                                 @RequestParam("id") Long id,
                                 @RequestParam(value = "fromAdminPage", defaultValue = "false") Boolean fromAdminPage,
                                 Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return urlWebService.getEditModelAndViewWithErrors(bindingResult, updateUrlRequest, id, fromAdminPage, authentication);
        }
        return urlWebService.updateUrl(id, updateUrlRequest, fromAdminPage, authentication);
    }

    @GetMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Long id, Authentication authentication) {
        urlService.deleteById(id, authentication);
        return "redirect:/V2/urls/list/user";
    }

    @GetMapping(value = "/prolongation/{id}")
    public String prolongation(@PathVariable("id") Long id, Authentication authentication) {
        urlService.prolongation(id, authentication);
        return "redirect:/V2/urls/list/user";
    }

    @GetMapping(value = "/create")
    public ModelAndView create(Authentication authentication) {
        ModelAndView result = new ModelAndView("create");
        result.addObject("username", authentication.getName());
        return result;
    }

    @PostMapping(value = "/create")
    public ModelAndView postCreate(@Valid @ModelAttribute CreateUrlRequest createUrlRequest,
                                   BindingResult bindingResult,
                                   Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return urlWebService.getCreateModelAndViewWithErrors(bindingResult, authentication);
        }
        return urlWebService.createUrl(createUrlRequest, authentication);
    }
}
