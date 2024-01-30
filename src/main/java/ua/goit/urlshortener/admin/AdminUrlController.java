package ua.goit.urlshortener.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.goit.urlshortener.url.UrlDto;

import java.util.List;

@RestController
@RequestMapping("V1/admin/urls")
@RequiredArgsConstructor
@Tag(name = "Admin urls", description = "Admin API for urls")
public class AdminUrlController {

    private final AdminService adminService;

    @GetMapping("/{userId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all urls for selected user")
    public List<UrlDto> getUrlsForSelectedUser(@PathVariable("userId") Long userId, Authentication authentication) {
        return adminService.getUrlsForSelectedUser(userId);
    }

    @GetMapping("/{userId}/active")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all active urls for selected user")
    public List<UrlDto> getActiveUsersUrls(@PathVariable("userId") Long userId, Authentication authentication) {
        return adminService.getActiveUrlsForSelectedUser(userId);
    }

    @GetMapping("/{userId}/inactive")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all inactive urls for selected user")
    public List<UrlDto> getInactiveUsersUrls(@PathVariable("userId") Long userId, Authentication authentication) {
        return adminService.getInactiveUrlsForSelectedUser(userId);
    }
}
