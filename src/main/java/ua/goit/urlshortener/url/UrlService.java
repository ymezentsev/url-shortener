package ua.goit.urlshortener.url;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.security.access.AccessDeniedException;
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
public class UrlService {
    private final UrlRepository urlRepository;
    private final UserService userService;
    private final ShortUrlGenerator shortUrlGenerator;

    public void createUrl(CreateShotUrlRequest createShotUrlRequest, String username) {
        String originalUrl = createShotUrlRequest.getOriginalUrl();

        if (!originalUrl.startsWith("https://") && !originalUrl.startsWith("http://")) {
            originalUrl = "https://" + originalUrl;
        }

        if (!isUrlAccessible(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL");
        }

        User user = userService.findByUsername(username);

        Url newUrl = Url.builder()
                .shortUrl(shortUrlGenerator.generateShortUrl())
                .url(originalUrl)
                .user(user)
                .visitCount(0)
                .build();
        urlRepository.save(newUrl);
    }

    private boolean isUrlAccessible(String originalUrl) {
        int responseCode;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(originalUrl).openConnection();
            connection.setRequestMethod("HEAD");
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
        }
        return responseCode >= 200 && responseCode < 300;
    }

    public List<UrlDto> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(Url::buildUrlDto)
                .collect(Collectors.toList());
    }

    public List<UrlDto> getAllUsersUrls(String username) {
        Long userId = userService.findByUsername(username).getId();

        return urlRepository.findByUserId(userId).stream()
                .map(Url::buildUrlDto)
                .collect(Collectors.toList());
    }

    public List<UrlDto> getAllUsersActiveUrls(String username) {
        Long userId = userService.findByUsername(username).getId();

        return urlRepository.findUsersActiveUrls(userId, LocalDateTime.now())
                .stream()
                .map(Url::buildUrlDto)
                .collect(Collectors.toList());
    }

    public List<UrlDto> getAllUsersNotActiveUrls(String username) {
        Long userId = userService.findByUsername(username).getId();

        return urlRepository.findUsersNotActiveUrls(userId, LocalDateTime.now())
                .stream()
                .map(Url::buildUrlDto)
                .collect(Collectors.toList());
    }

    public String redirectToUrl(String shotUrl) {
        if (!urlRepository.existsById(shotUrl)) {
            throw new IllegalArgumentException("Url with id " + shotUrl + " not found");
        }

        Url url = urlRepository.findById(shotUrl).orElseThrow();
        url.incrementVisitCount();
        url.setExpirationDate(LocalDateTime.now().plusDays(Url.VALID_DAYS));
        urlRepository.save(url);
        return url.getUrl();
    }

    public void deleteByShotUrl(String username, String shotUrl) {
        if (!urlRepository.existsById(shotUrl)) {
            throw new IllegalArgumentException("Url with id " + shotUrl + " not found");
        } else {
            Optional<Url> optionalUrl = urlRepository.findById(shotUrl);
            if (optionalUrl.isPresent()) {
                Url urlToDelete = optionalUrl.get();
                if (!urlToDelete.getUser().getUsername().equals(username)) {
                    throw new AccessDeniedException("Access forbidden");
                }

                User user = urlToDelete.getUser();
                user.getUrls().remove(urlToDelete);
            }
            urlRepository.deleteById(shotUrl);
        }
    }
}
