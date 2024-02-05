package ua.goit.urlshortener.mvc.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.admin.AdminService;

import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@Service
@RequiredArgsConstructor
public class AdminUserWebService {
    private final AdminService adminService;

    public ModelAndView deleteUser(Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ADMIN_USERS);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());

        try {
            adminService.deleteUserById(id, authentication);
        } catch (Exception e) {
            result.addObject(ATTRIBUTE_ERRORS, e.getMessage());
        }
        result.addObject(ATTRIBUTE_USERS, adminService.getAllUsers());
        return result;
    }

    public ModelAndView changeUserRole(Long id, Authentication authentication) {
        ModelAndView result = new ModelAndView(MODEL_ADMIN_USERS);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());

        try {
            adminService.changeUserRole(id, authentication);
        } catch (Exception e) {
            result.addObject(ATTRIBUTE_ERRORS, e.getMessage());
        }
        result.addObject(ATTRIBUTE_USERS, adminService.getAllUsers());
        return result;
    }
}
