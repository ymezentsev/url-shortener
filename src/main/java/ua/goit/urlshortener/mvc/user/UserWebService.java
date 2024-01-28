package ua.goit.urlshortener.mvc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.url.service.UrlService;
import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWebService {
    private final UserService userService;
    private final UrlService urlService;

    public ModelAndView registerUser(CreateUserRequest userRequest) {
        try {
            userService.registerUser(userRequest);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView("register");
            result.addObject("errors", e.getMessage());
            return result;
        }
        return new ModelAndView("success");
    }

    public ModelAndView loginUser(CreateUserRequest userRequest) {
        try {
            userService.loginUser(userRequest);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView("login");
            result.addObject("errors", "Wrong username or password");
            return result;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ModelAndView result = new ModelAndView("all-user");
        result.addObject("username", authentication.getName());
        result.addObject("userUrls", urlService.getActiveUrlUser(authentication));
        result.addObject("userUrlsInactive", urlService.getInactiveUrlUser(authentication));
        return result;
    }

    public ModelAndView getModelAndViewWithErrors(BindingResult bindingResult, ModelAndView result) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        result.addObject("errors", errors);
        return result;
    }
}
