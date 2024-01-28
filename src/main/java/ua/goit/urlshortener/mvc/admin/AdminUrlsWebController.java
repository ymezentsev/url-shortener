package ua.goit.urlshortener.mvc.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.mvc.url.UrlWebService;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

@Controller
@RequestMapping("V2/admin/urls")
@RequiredArgsConstructor
public class AdminUrlsWebController {
    private final UrlService urlService;
    private final UrlWebService urlWebService;

    @GetMapping()
    public ModelAndView getAllLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView("admin-urls");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrl());
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/active")
    public ModelAndView getAllUsersActiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView("admin-urls");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrl());
        return result;
    }

    @GetMapping("/inactive")
    public ModelAndView getAllUsersInactiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView("admin-urls");
        result.addObject("username", authentication.getName());
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Long id, Authentication authentication) {
        urlService.deleteById(id, authentication);
        return "redirect:/V2/admin/urls";
    }

    @GetMapping(value = "/prolongation/{id}")
    public String prolongation(@PathVariable("id") Long id, Authentication authentication) {
        urlService.prolongation(id, authentication);
        return "redirect:/V2/admin/urls";
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView("edit");
        result.addObject("username", authentication.getName());
        result.addObject("urls", urlService.getById(id));
        result.addObject("id", id);
        result.addObject("fromAdminPage", "true");
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
}
