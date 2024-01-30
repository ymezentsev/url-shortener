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

import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@Service
@RequiredArgsConstructor
public class UserWebService {
    private final UserService userService;
    private final UrlService urlService;

    public ModelAndView registerUser(CreateUserRequest userRequest) {
        try {
            userService.registerUser(userRequest);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView(MODEL_REGISTER);
            result.addObject(ATTRIBUTE_ERRORS, e.getMessage());
            return result;
        }
        return new ModelAndView(MODEL_SUCCESS);
    }

    public ModelAndView loginUser(CreateUserRequest userRequest) {
        try {
            userService.loginUser(userRequest);
        } catch (Exception e) {
            ModelAndView result = new ModelAndView(MODEL_LOGIN);
            result.addObject(ATTRIBUTE_ERRORS, "Wrong username or password");
            return result;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ModelAndView result = new ModelAndView(MODEL_ALL_USER);
        result.addObject(ATTRIBUTE_USERNAME, authentication.getName());
        result.addObject(ATTRIBUTE_USER_URLS, urlService.getActiveUrlUser(authentication));
        result.addObject(ATTRIBUTE_USER_URLS_INACTIVE, urlService.getInactiveUrlUser(authentication));
        return result;
    }

    public ModelAndView getModelAndViewWithErrors(BindingResult bindingResult, ModelAndView result) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        result.addObject(ATTRIBUTE_ERRORS, errors);
        return result;
    }
}
