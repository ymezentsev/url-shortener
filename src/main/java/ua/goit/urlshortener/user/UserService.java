package ua.goit.urlshortener.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.jwt.JwtUtils;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public UserDto findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("There isn't user with username: " + username));
        return userMapper.toUserDto(userEntity);
    }

    public void registerUser(CreateUserRequest userRequest) {
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();

        if (Boolean.TRUE.equals(userRepository.existsByUsername(username))) {
            throw new UserAlreadyExistException(username);
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(encoder.encode(password))
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    public String loginUser(CreateUserRequest userRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        userRequest.getUsername(), userRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userRequest.getUsername());
        return jwtUtils.generateJwtToken(userDetails);
    }
}
