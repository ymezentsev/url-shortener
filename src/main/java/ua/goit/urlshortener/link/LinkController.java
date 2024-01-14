package ua.goit.urlshortener.link;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    @GetMapping
    public String test(){
        return "sd";
    }

    @PostMapping("/create")
    public void createShortLink(@RequestBody CreateShotLinkRequest createShotLinkRequest){
        String username = "user";
        linkService.createLink(createShotLinkRequest, username);
    }

    @GetMapping("/all")
    public List<LinkDto> getAllLinks(){
        return linkService.getAllLinks();
    }

    @GetMapping("/all/user")
    public List<LinkDto> getAllUsersLinks(){
        Long userId = 2L;
        return linkService.getAllUsersLinks(userId);
    }

    @GetMapping("/all/user/active")
    public List<LinkDto> getAllUsersActiveLinks(){
        Long userId = 2L;
        return linkService.getAllUsersActiveLinks(userId);
    }

    @GetMapping("/all/user/not_active")
    public List<LinkDto> getAllUsersNotActiveLinks(){
        Long userId = 2L;
        return linkService.getAllUsersNotActiveLinks(userId);
    }

    @GetMapping("/{shotUrl}")
    public String redirectToLink(@PathVariable("shotUrl") String shotUrl){
        return linkService.redirectToLink(shotUrl);
    }

    @DeleteMapping("/delete/{shotUrl}")
    public void deleteByShotUrl(@PathVariable("shotUrl") String shotUrl){
        linkService.deleteByShotUrl(shotUrl);
    }
}
