package ua.goit.urlshortener.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.user.User;
import ua.goit.urlshortener.user.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkService {
    private static final int DAYS_TO_EXTENDS = 30;
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
                .shortUrl(shortLinkGenerator.generateShortLink())
                .url(originalLink)
                .user(user)
                .visitCount(0)
                .build();
        linkRepository.save(newlink);
    }

    private boolean isLinkAccessible(String originalLink) {
        int responseCode;

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

    public List<LinkDto> getAllLinks() {
        return linkRepository.findAll().stream()
                .map(Link::buildLinkDto)
                .collect(Collectors.toList());
    }

    public List<LinkDto> getAllUsersLinks(Long userId) {
        return linkRepository.findByUserId(userId).stream()
                .map(Link::buildLinkDto)
                .collect(Collectors.toList());
    }

    public List<LinkDto> getAllUsersActiveLinks(Long userId) {
        return linkRepository.findUsersActiveLinks(userId, LocalDateTime.now())
                .stream()
                .map(Link::buildLinkDto)
                .collect(Collectors.toList());
    }

    public List<LinkDto> getAllUsersNotActiveLinks(Long userId) {
        return linkRepository.findUsersNotActiveLinks(userId, LocalDateTime.now())
                .stream()
                .map(Link::buildLinkDto)
                .collect(Collectors.toList());
    }

    public String redirectToLink(String shotUrl) {
        if (!linkRepository.existsById(shotUrl)) {
            throw new IllegalArgumentException("Link with id " + shotUrl + " not found");
        }

        Link link = linkRepository.findById(shotUrl).orElseThrow();
        link.incrementVisitCount();
        link.setExpirationDate(LocalDateTime.now().plusDays(DAYS_TO_EXTENDS));
        linkRepository.save(link);
        return link.getUrl();
    }

    public void deleteByShotUrl(String shotUrl) {
        if (!linkRepository.existsById(shotUrl)) {
            throw new IllegalArgumentException("Link with id " + shotUrl + " not found");
        } else {
            Optional<Link> optionalLink = linkRepository.findById(shotUrl);
            if (optionalLink.isPresent()) {
                Link linkToDelete = optionalLink.get();
                User user = linkToDelete.getUser();
                user.getLinks().remove(linkToDelete);
            }
            linkRepository.deleteById(shotUrl);
        }
    }

}
