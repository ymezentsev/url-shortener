package ua.goit.urlshortener.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.user.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional
    public void deleteById(Long id, Authentication authentication) {
        UserEntity userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (userToDelete.getUsername().equals(authentication.getName())) {
            throw new IllegalArgumentException("You can't delete yourself");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void changeRole(Long id, Authentication authentication) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (userToUpdate.getUsername().equals(authentication.getName())) {
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
