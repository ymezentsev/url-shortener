package ua.goit.urlshortener.url.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.goit.urlshortener.url.UrlDto;
import ua.goit.urlshortener.url.UrlEntity;
import ua.goit.urlshortener.url.UrlMapper;
import ua.goit.urlshortener.url.UrlRepository;
import ua.goit.urlshortener.url.exceptions.AlreadyExistUrlException;
import ua.goit.urlshortener.url.exceptions.NotAccessibleException;
import ua.goit.urlshortener.url.request.CreateUrlRequest;
import ua.goit.urlshortener.url.request.UpdateUrlRequest;
import ua.goit.urlshortener.user.Role;
import ua.goit.urlshortener.user.UserEntity;
import ua.goit.urlshortener.user.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static ua.goit.urlshortener.url.UrlEntity.VALID_DAYS;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final ShortLinkGenerator shortLinkGenerator;
    private final UserService userService;

    private static final String ACCESS_FORBIDDEN = "Access forbidden";
    private static final String URL_NOT_FOUND = "Url with id %d not found";

    public List<UrlDto> getAll() {
        return urlMapper.toUrlDtoList(urlRepository.findAll());
    }

    @Transactional
    public UrlDto createUrl(CreateUrlRequest url, Authentication authentication) {
        String originalUrl = getFullUrl(url.getUrl());

        if (!isUrlAccessible(originalUrl)) {
            throw new NotAccessibleException(originalUrl);
        }

        String generatedShortUrl;
        do {
            generatedShortUrl = shortLinkGenerator.generateShortLink();
        } while (!isLinkUnique(generatedShortUrl));

        UrlDto urlDto = UrlDto.builder()
                .shortUrl(generatedShortUrl)
                .url(originalUrl)
                .description(url.getDescription())
                .username(authentication.getName())
                .visitCount(0)
                .build();
        return urlMapper.toUrlDto(urlRepository.save(urlMapper.toUrlEntity(urlDto)));
    }

    public void deleteById(Long id, Authentication authentication) {
        UrlEntity urlToDelete = urlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(URL_NOT_FOUND, id)));

        if (!urlToDelete.getUser().getUsername().equals(authentication.getName()) &&
                !getAuthority(authentication).equals(Role.ADMIN.name())) {
            throw new AccessDeniedException(ACCESS_FORBIDDEN);
        }

        UserEntity user = urlToDelete.getUser();
        user.getUrls().remove(urlToDelete);
        urlRepository.deleteById(id);
    }

    public void updateUrl(Long id, UpdateUrlRequest request, Authentication authentication) {
        UrlEntity urlToUpdate = urlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(URL_NOT_FOUND, id)));

        if (!urlToUpdate.getUser().getUsername().equals(authentication.getName()) &&
                !getAuthority(authentication).equals(Role.ADMIN.name())) {
            throw new AccessDeniedException(ACCESS_FORBIDDEN);
        }

        if (!isLinkUnique(request.getShortUrl())) {
            Long idExistingLink = urlRepository.findByShortUrl(request.getShortUrl())
                    .orElseThrow()
                    .getId();
            if (!Objects.equals(idExistingLink, id)) {
                throw new AlreadyExistUrlException(request.getShortUrl());
            }
        }

        if (request.getShortUrl().isBlank()) {
            throw new IllegalArgumentException("Short link can't be empty");
        }

        if (!isUrlAccessible(getFullUrl(request.getUrl()))) {
            throw new NotAccessibleException(request.getUrl());
        }

        urlToUpdate.setUrl(getFullUrl(request.getUrl()));
        urlToUpdate.setShortUrl(request.getShortUrl());
        urlToUpdate.setDescription(request.getDescription());
        urlRepository.save(urlToUpdate);
    }

    public UrlDto getById(Long id) {
        return urlMapper.toUrlDto(urlRepository.getReferenceById(id));
    }

    public List<UrlDto> getAllUrlUser(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        return urlMapper.toUrlDtoList(urlRepository.findByUserId(userId));
    }

    public List<UrlDto> getActiveUrlUser(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        LocalDate currentDate = LocalDate.now();
        return urlMapper.toUrlDtoList(urlRepository.findActiveUrlsByUserId(userId, currentDate));
    }

    public List<UrlDto> getInactiveUrlUser(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        LocalDate currentDate = LocalDate.now();
        return urlMapper.toUrlDtoList(urlRepository.findInactiveUrlsByUserId(userId, currentDate));
    }

    public List<UrlDto> getActiveUrl() {
        LocalDate currentDate = LocalDate.now();
        return urlMapper.toUrlDtoList(urlRepository.findActiveUrls(currentDate));
    }

    public List<UrlDto> getInactiveUrl() {
        LocalDate currentDate = LocalDate.now();
        return urlMapper.toUrlDtoList(urlRepository.findInactiveUrls(currentDate));
    }

    public void prolongation(Long id, Authentication authentication) {
        UrlEntity urlToProlong = urlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(URL_NOT_FOUND, id)));

        if (!urlToProlong.getUser().getUsername().equals(authentication.getName()) &&
                !getAuthority(authentication).equals(Role.ADMIN.name())) {
            throw new AccessDeniedException(ACCESS_FORBIDDEN);
        }

        LocalDate newExpirationDate = urlToProlong.getExpirationDate().plusDays(VALID_DAYS);
        if (newExpirationDate.isBefore(LocalDate.now())) {
            newExpirationDate = LocalDate.now().plusDays(VALID_DAYS);
        }
        urlToProlong.setExpirationDate(newExpirationDate);
        urlRepository.save(urlToProlong);
    }

    public void redirectToUrl(String shotUrl, HttpServletResponse response) throws IOException {
        UrlEntity urlEntity = urlRepository.findByShortUrl(shotUrl)
                .orElseThrow(() -> new IllegalArgumentException("Url with short url = " + shotUrl + " not found"));

        if (urlEntity.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Link is inactive");
        }

        urlEntity.setVisitCount(urlEntity.getVisitCount() + 1);
        urlEntity.setExpirationDate(LocalDate.now().plusDays(VALID_DAYS));

        urlRepository.save(urlEntity);
        response.sendRedirect(urlEntity.getUrl());
    }

    private String getAuthority(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow()
                .getAuthority();
    }

    private boolean isLinkUnique(String link) {
        return getAll().stream().noneMatch(urlEntity -> urlEntity.getShortUrl().equals(link));
    }

    private String getFullUrl(String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "https://" + url;
        }
        return url;
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
}
