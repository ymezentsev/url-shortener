package ua.goit.urlshortener.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("V1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public void registerUser(@Valid @RequestBody CreateUserRequest userRequest) {
        userService.registerUser(userRequest);
    }

/*    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody CreateUserRequest userRequest) {
     //   return ResponseEntity.ok(userService.loginUser(userRequest));
    }*/

}
