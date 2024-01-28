package ua.goit.urlshortener.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.goit.urlshortener.user.UserDto;

import java.util.List;

@RestController
@RequestMapping("V1/admin/users")
@RequiredArgsConstructor
@Tag(name= "Admin", description = "Admin API")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/list")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all users")
    public List<UserDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PostMapping("/edit/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Change user's role")
    public void changeUserRole(@PathVariable("id") Long id, Authentication authentication) {
        adminService.changeRole(id, authentication);
    }

    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete user")
    public void deleteUser(@PathVariable("id") Long id, Authentication authentication) {
        adminService.deleteById(id, authentication);
    }
}
