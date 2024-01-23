package ua.goit.urlshortener.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.jwt.JwtUtils;
import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.Role;
import ua.goit.urlshortener.user.UserAlreadyExistException;
import ua.goit.urlshortener.user.UserEntity;
import ua.goit.urlshortener.user.UserDto;
import ua.goit.urlshortener.user.UserMapper;
import ua.goit.urlshortener.user.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserDto findByUsername(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            return userMapper.toUserDto(userEntity.get());
        } else {
            throw new NoSuchElementException("There isn't user with username: " + username);
        }
    }

    @Override
    public void registerUser(CreateUserRequest userRequest) {
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();


        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistException(username);
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(encoder.encode(password))
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    @Override
    public String loginUser(CreateUserRequest userRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        userRequest.getUsername(), userRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }
}
