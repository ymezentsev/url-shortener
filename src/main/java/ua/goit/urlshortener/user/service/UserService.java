package ua.goit.urlshortener.user.service;

import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserDto;

public interface UserService {
    public UserDto findByUsername(String username);
    void registerUser(CreateUserRequest userRequest);
    String loginUser(CreateUserRequest userRequest);
}
