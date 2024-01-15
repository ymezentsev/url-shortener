package ua.goit.urlshortener.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByUsername (String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()->new NoSuchElementException("User not found"));
    }
}
