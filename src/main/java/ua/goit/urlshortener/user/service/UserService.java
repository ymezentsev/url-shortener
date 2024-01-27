package ua.goit.urlshortener.user.service;

import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto findByUsername(String username);

    void registerUser(CreateUserRequest userRequest);

    String loginUser(CreateUserRequest userRequest);

    List<UserDto> listAll();

    void deleteById(String username, Long id);

    void changeRole(String username, Long id);

}
