package ua.goit.urlshortener.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.user.exception.UserAlreadyExistException;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //TODO change when add JWT
    //private final PasswordEncoder encoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public void registerUser(CreateUserRequest userRequest) {
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistException(username);
        }

        User user = User.builder()
                .username(username)
                //TODO change when add JWT
                //.password(encoder.encode(password))
                .password(password)
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }
}
