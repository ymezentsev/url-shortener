package ua.goit.urlshortener.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.user.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("V2/admin/users")
@RequiredArgsConstructor
public class AdminUsersWebController {
    private final UserService userService;
    private final AdminUserWebService adminUserWebService;
    @GetMapping()
    public ModelAndView getAllUsers(Principal principal) {
        ModelAndView result = new ModelAndView("admin-users");
        result.addObject("username", principal.getName());
        result.addObject("users", userService.listAll());
        return result;
    }

    @GetMapping(value = "/delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id, Principal principal) {
        return adminUserWebService.delete(principal, id);
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView setRole(@PathVariable("id") Long id, Principal principal) {
        return adminUserWebService.changeRole(principal, id);
    }
}
