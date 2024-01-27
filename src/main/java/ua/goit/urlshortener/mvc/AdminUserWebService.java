package ua.goit.urlshortener.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.user.service.UserService;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AdminUserWebService {
    private final UserService userService;

    public ModelAndView delete(Principal principal, Long id) {
        ModelAndView result = new ModelAndView("admin-users");
        result.addObject("username", principal.getName());

        try {
            userService.deleteById(principal.getName(), id);
        } catch (Exception e) {
            result.addObject("errors", e.getMessage());
        }
        result.addObject("users", userService.listAll());
        return result;
    }

    public ModelAndView changeRole(Principal principal, Long id) {
        ModelAndView result = new ModelAndView("admin-users");
        result.addObject("username", principal.getName());

        try {
            userService.changeRole(principal.getName(), id);
        } catch (Exception e) {
            result.addObject("errors", e.getMessage());
        }
        result.addObject("users", userService.listAll());
        return result;
    }
}
