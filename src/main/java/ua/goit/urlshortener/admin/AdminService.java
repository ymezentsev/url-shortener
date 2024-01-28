package ua.goit.urlshortener.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.url.UrlDto;
import ua.goit.urlshortener.url.UrlMapper;
import ua.goit.urlshortener.url.UrlRepository;
import ua.goit.urlshortener.user.*;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final UrlRepository urlRepository;
    private final UserMapper userMapper;
    private final UrlMapper urlMapper;

    public List<UserDto> getAllUsers() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional
    public void deleteUserById(Long id, Authentication authentication) {
        UserEntity userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (userToDelete.getUsername().equals(authentication.getName())) {
            throw new IllegalArgumentException("You can't delete yourself");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void changeUserRole(Long id, Authentication authentication) {
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

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UrlDto> getUrlsForSelectedUser(Long userId) {
        return urlMapper.toUrlDtoList(urlRepository.findByUserId(userId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UrlDto> getActiveUrlsForSelectedUser(Long userId) {
        LocalDate currentDate = LocalDate.now();
        return urlMapper.toUrlDtoList(urlRepository.findActiveUrlsByUserId(userId, currentDate));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UrlDto> getInactiveUrlsForSelectedUser(Long userId) {
        LocalDate currentDate = LocalDate.now();
        return urlMapper.toUrlDtoList(urlRepository.findInactiveUrlsByUserId(userId, currentDate));
    }
}
