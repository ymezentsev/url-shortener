package ua.goit.urlshortener.mvc.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.admin.AdminService;
import ua.goit.urlshortener.mvc.url.UrlWebService;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@Controller
@RequestMapping("V2/admin/urls")
@RequiredArgsConstructor
public class AdminUrlsWebController {
    private final UrlService urlService;
    private final UrlWebService urlWebService;
    private final AdminService adminService;

    @GetMapping()
    public ModelAndView getAllLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ADMIN_URLS);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USERS, adminService.getAllUsers());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrl());
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrl());
        return result;
    }

    @GetMapping("/active")
    public ModelAndView getAllUsersActiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ADMIN_URLS);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USERS, adminService.getAllUsers());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrl());
        return result;
    }

    @GetMapping("/inactive")
    public ModelAndView getAllUsersInactiveLinks(Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ADMIN_URLS);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USERS, adminService.getAllUsers());
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrl());
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
        ModelAndView result = new ModelAndView(MODEL_EDIT);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getById(id));
        result.addObject(ATTRIBUTE_ID, id);
        result.addObject(ATTRIBUTE_FROM_ADMIN_PAGE, "true");
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

    @GetMapping("/{userId}")
    public ModelAndView allUserUrls(@PathVariable("userId") Long userId, Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ADMIN_URLS);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USERS, adminService.getAllUsers());
        result.addObject(ATTRIBUTE_USER_URLS, adminService.getActiveUrlsForSelectedUser(userId));
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, adminService.getInactiveUrlsForSelectedUser(userId));
        return result;
    }
}
