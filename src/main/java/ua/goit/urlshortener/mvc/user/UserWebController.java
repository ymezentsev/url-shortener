package ua.goit.urlshortener.mvc.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.user.CreateUserRequest;

import static ua.goit.urlshortener.mvc.ConstantsStorage.*;

@Controller
@RequestMapping("V2/user")
@RequiredArgsConstructor
public class UserWebController {
    private final UserWebService userWebService;

    @GetMapping("/register")
    public String getRegisterUser() {
        return MODEL_REGISTER;
    }

    @PostMapping("/register")
    public ModelAndView postRegisterUser(@Valid @ModelAttribute CreateUserRequest userRequest,
                                         BindingResult bindingResult,
                                         @RequestParam(name = "register", required = false) Boolean register) {
        ModelAndView result = new ModelAndView(MODEL_REGISTER);
        if (bindingResult.hasErrors()) {
            return userWebService.getModelAndViewWithErrors(bindingResult, result);
        }
        return userWebService.registerUser(userRequest);
    }

    @GetMapping("/login")
    public String getLoginUser() {
        return MODEL_LOGIN;
    }

    @PostMapping("/login")
    public ModelAndView postLoginUser(@Valid @ModelAttribute CreateUserRequest userRequest,
                                      BindingResult bindingResult) {
        ModelAndView result = new ModelAndView(MODEL_LOGIN);
        if (bindingResult.hasErrors()) {
            return userWebService.getModelAndViewWithErrors(bindingResult, result);
        }
        return userWebService.loginUser(userRequest);
    }
}