package ua.goit.urlshortener.url;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.url.service.UrlService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/V1/urls")
@RequiredArgsConstructor
@Tag(name = "Url", description = "API to work with Urls")
public class UrlController {
    private final UrlService urlService;

    @GetMapping()
    @Operation(summary = "Get all urls")
    public List<UrlDto> getAllUrl() {
        return urlService.getAll();
    }

    @PostMapping("/create")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Create short url")
    public UrlDto createUrl(@RequestBody CreateUrlRequest request, Authentication authentication) {
        return urlService.createUrl(request, authentication);
    }

    @GetMapping("/current")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all urls for current user")
    public List<UrlDto> getAllUserUrls(Authentication authentication) {
        return urlService.getAllUrlUser(authentication);
    }

    @PostMapping("/edit/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Url edit")
    public void updateUrl(@PathVariable("id") Long id,
                          @RequestBody UpdateUrlRequest request,
                          Authentication authentication) {
        urlService.updateUrl(id, request, authentication);
    }

    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete url")
    public void deleteUrl(@PathVariable("id") Long id, Authentication authentication) {
        urlService.deleteById(id, authentication);
    }

    @PostMapping("/prolongation/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Prolongation url's expiration date")
    public void prolongUrl(@PathVariable("id") Long id, Authentication authentication) {
        urlService.prolongation(id, authentication);
    }

    @GetMapping("/current/active")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all active urls for current user")
    public List<UrlDto> getUserActiveUrls(Authentication authentication) {
        return urlService.getUserActiveUrl(authentication);
    }

    @GetMapping("/current/inactive")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all inactive urls for current user")
    public List<UrlDto> getUserInactiveUrls(Authentication authentication) {
        return urlService.getUserInactiveUrl(authentication);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active urls")
    public List<UrlDto> getActiveUrls() {
        return urlService.getActiveUrl();
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get all inactive urls")
    public List<UrlDto> getInactiveUrls() {
        return urlService.getInactiveUrl();
    }

    @GetMapping("/{shortUrl}")
    @Operation(summary = "Redirect by short url")
    public void redirectToUrl(@PathVariable("shortUrl") String shortUrl, HttpServletResponse response) throws IOException {
        urlService.redirectToUrl(shortUrl, response);
    }
}
