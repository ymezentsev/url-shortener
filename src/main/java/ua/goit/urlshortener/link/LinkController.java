package ua.goit.urlshortener.link;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        String username = "admin";
        linkService.createLink(createShotLinkRequest, username);
    }
}
