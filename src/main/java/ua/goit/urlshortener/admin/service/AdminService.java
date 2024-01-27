package ua.goit.urlshortener.admin.service;

import ua.goit.urlshortener.user.CreateUserRequest;
import ua.goit.urlshortener.user.UserDto;

import java.util.List;

public interface AdminService {
    List<UserDto> listAll();

    void deleteById(String username, Long id);

    void changeRole(String username, Long id);
}
