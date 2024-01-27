package ua.goit.urlshortener.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.jwt.JwtUtils;
import ua.goit.urlshortener.user.*;

import java.util.List;
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

    @Override
    public List<UserDto> listAll() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteById(String username, Long id) {
        UserEntity userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (userToDelete.getUsername().equals(username)) {
            throw new IllegalArgumentException("You can't delete yourself");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void changeRole(String username, Long id) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (userToUpdate.getUsername().equals(username)) {
            throw new IllegalArgumentException("You can't change role for yourself");
        }

        if (userToUpdate.getRole().equals(Role.ADMIN)) {
            userToUpdate.setRole(Role.USER);
        } else {
            userToUpdate.setRole(Role.ADMIN);
        }
        userRepository.save(userToUpdate);
    }
}
