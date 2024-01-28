package ua.goit.urlshortener.mvc.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.admin.AdminService;

@Service
@RequiredArgsConstructor
public class AdminUserWebService {
    private final AdminService adminService;

    public ModelAndView delete(Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView("admin-users");
        result.addObject("username", authentication.getName());

        try {
            adminService.deleteUserById(id, authentication);
        } catch (Exception e) {
            result.addObject("errors", e.getMessage());
        }
        result.addObject("users", adminService.getAllUsers());
        return result;
    }

    public ModelAndView changeRole(Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView("admin-users");
        result.addObject("username", authentication.getName());

        try {
            adminService.changeUserRole(id, authentication);
        } catch (Exception e) {
            result.addObject("errors", e.getMessage());
        }
        result.addObject("users", adminService.getAllUsers());
        return result;
    }
}
