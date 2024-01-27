package ua.goit.urlshortener.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

import java.security.Principal;

@Controller
@RequestMapping("V2/admin/urls")
@RequiredArgsConstructor
public class AdminUrlsWebController {
    private final UrlService urlService;
    private final UrlWebService urlWebService;

    @GetMapping()
    public ModelAndView getAllLinks(Principal principal) {
        ModelAndView result = new ModelAndView("admin-urls");
        result.addObject("username", principal.getName());
        result.addObject("userUrls", urlService.getActiveUrl());
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/active")
    public ModelAndView getAllUsersActiveLinks(Principal principal) {
        ModelAndView result = new ModelAndView("admin-urls");
        result.addObject("username", principal.getName());
        result.addObject("userUrls", urlService.getActiveUrl());
        return result;
    }

    @GetMapping("/inactive")
    public ModelAndView getAllUsersInactiveLinks(Principal principal) {
        ModelAndView result = new ModelAndView("admin-urls");
        result.addObject("username", principal.getName());
        result.addObject("userUrlsInactive", urlService.getInactiveUrl());
        return result;
    }

    @GetMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        urlService.deleteById(principal.getName(), id);
        return "redirect:/V2/admin/urls";
    }

    @GetMapping(value = "/prolongation/{id}")
    public String prolongation(@PathVariable("id") Long id, Principal principal) {
        urlService.prolongation(principal.getName(), id);
        return "redirect:/V2/admin/urls";
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, Principal principal) {
        ModelAndView result = new ModelAndView("edit");
        result.addObject("username", principal.getName());
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
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return urlWebService.getEditModelAndViewWithErrors(bindingResult, updateUrlRequest, id, fromAdminPage, principal);
        }
        return urlWebService.updateUrl(principal, id, updateUrlRequest, fromAdminPage);
    }
}
