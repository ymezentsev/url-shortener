package ua.goit.urlshortener.url;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("V1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/create")
    public void createShortUrl(@RequestBody CreateShotUrlRequest createShotUrlRequest){
        String username = "user";
        urlService.createUrl(createShotUrlRequest, username);
    }

    @GetMapping("/all")
    public List<UrlDto> getAllUrls(){
        return urlService.getAllUrls();
    }

    @GetMapping("/all/user")
    public List<UrlDto> getAllUsersUrls(){
        Long userId = 2L;
        return urlService.getAllUsersUrls(userId);
    }

    @GetMapping("/all/user/active")
    public List<UrlDto> getAllUsersActiveUrls(){
        Long userId = 2L;
        return urlService.getAllUsersActiveUrls(userId);
    }

    @GetMapping("/all/user/not_active")
    public List<UrlDto> getAllUsersNotActiveUrls(){
        Long userId = 2L;
        return urlService.getAllUsersNotActiveUrls(userId);
    }

    @GetMapping("/{shotUrl}")
    public String redirectToUrl(@PathVariable("shotUrl") String shotUrl){
        return urlService.redirectToUrl(shotUrl);
    }

    @DeleteMapping("/delete/{shotUrl}")
    public void deleteByShotUrl(@PathVariable("shotUrl") String shotUrl){
        urlService.deleteByShotUrl(shotUrl);
    }
}
