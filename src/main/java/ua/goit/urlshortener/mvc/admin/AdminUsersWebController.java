package ua.goit.urlshortener.mvc.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.admin.AdminService;

@Controller
@RequestMapping("V2/admin/users")
@RequiredArgsConstructor
public class AdminUsersWebController {
    private final AdminService adminService;
    private final AdminUserWebService adminUserWebService;

    @GetMapping()
    public ModelAndView getAllUsers(Authentication authentication) {
        ModelAndView result = new ModelAndView("admin-users");
        result.addObject("username", authentication.getName());
        result.addObject("users", adminService.getAllUsers());
        return result;
    }

    @GetMapping(value = "/delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id, Authentication authentication) {
        return adminUserWebService.delete(id, authentication);
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView setRole(@PathVariable("id") Long id, Authentication authentication) {
        return adminUserWebService.changeRole(id, authentication);
    }
}
