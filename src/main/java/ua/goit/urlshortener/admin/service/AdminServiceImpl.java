package ua.goit.urlshortener.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.user.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
