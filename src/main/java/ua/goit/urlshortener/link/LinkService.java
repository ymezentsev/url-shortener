package ua.goit.urlshortener.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.user.User;
import ua.goit.urlshortener.user.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final UserService userService;
    private final ShortLinkGenerator shortLinkGenerator;

    public void createLink(CreateShotLinkRequest createShotLinkRequest, String username) {
        String originalLink = createShotLinkRequest.getOriginalLink();

        if (!originalLink.startsWith("https://") && !originalLink.startsWith("http://")) {
            originalLink = "https://" + originalLink;
        }

        if (!isLinkAccessible(originalLink)) {
            throw new IllegalArgumentException("Invalid URL");
        }

        User user = userService.findByUsername(username);

        Link newlink = Link.builder()
                .url(originalLink)
                .shortUrl(shortLinkGenerator.generateShortLink())
                .user(user)
                .visitCount(0)
                .build();
        linkRepository.save(newlink);
    }

    private boolean isLinkAccessible(String originalLink) {
        int responseCode = 0;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(originalLink).openConnection();
            connection.setRequestMethod("HEAD");
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
        }
        return responseCode >= 200 && responseCode < 300;
    }
}
