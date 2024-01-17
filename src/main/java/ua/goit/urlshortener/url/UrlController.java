package ua.goit.urlshortener.url;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("V1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/create")
    public void createShortUrl(@RequestBody CreateShotUrlRequest createShotUrlRequest){
        UserDetails principal = getUserDetails();
        urlService.createUrl(createShotUrlRequest, principal.getUsername());
    }

    @GetMapping("/all")
    public List<UrlDto> getAllUrls(){
        return urlService.getAllUrls();
    }

    @GetMapping("/all/user")
    public List<UrlDto> getAllUsersUrls(){
        UserDetails principal = getUserDetails();
        return urlService.getAllUsersUrls(principal.getUsername());
    }

    @GetMapping("/all/user/active")
    public List<UrlDto> getAllUsersActiveUrls(){
        UserDetails principal = getUserDetails();
        return urlService.getAllUsersActiveUrls(principal.getUsername());
    }

    @GetMapping("/all/user/not_active")
    public List<UrlDto> getAllUsersNotActiveUrls(){
        UserDetails principal = getUserDetails();
        return urlService.getAllUsersNotActiveUrls(principal.getUsername());
    }

    @GetMapping("/{shotUrl}")
    public String redirectToUrl(@PathVariable("shotUrl") String shotUrl){
        return urlService.redirectToUrl(shotUrl);
    }

    @DeleteMapping("/delete/{shotUrl}")
    public void deleteByShotUrl(@PathVariable("shotUrl") String shotUrl){
        UserDetails principal = getUserDetails();
        urlService.deleteByShotUrl(principal.getUsername(), shotUrl);
    }

    private UserDetails getUserDetails() {
        SecurityContext context = SecurityContextHolder.getContext();
        return (UserDetails) context.getAuthentication().getPrincipal();
    }
}
