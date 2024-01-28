package ua.goit.urlshortener.url.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.goit.urlshortener.url.UrlEntity;
import ua.goit.urlshortener.url.UrlDto;
import ua.goit.urlshortener.url.UrlMapper;
import ua.goit.urlshortener.url.UrlRepository;
import ua.goit.urlshortener.user.Role;
import ua.goit.urlshortener.user.UserDto;
import ua.goit.urlshortener.user.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UrlService urlService;



    @Test
    void testGetAllUrlUser() {

        Long userId = 1L;
        String username = "user";
        UserDto userDto = new UserDto(1L, "user", "password", Role.USER);

        List<UrlEntity> mockUrls = Arrays.asList(
                new UrlEntity(1L, "abc123", "https://www.example.com/1", "null", null,
                        LocalDate.now(), LocalDate.now().plusDays(30L), 0),
                new UrlEntity(2L, "def456", "https://www.example.com/2", "null", null,
                        LocalDate.now(), LocalDate.now().plusDays(30L), 0)
        );

        List<UrlDto> mockUrlDtos = Arrays.asList(
                createUrlDto(1L, "abc123", "https://www.example.com/1", "null", null,
                        LocalDate.now(), LocalDate.now().plusDays(30L), 0),
                createUrlDto(2L, "def456", "https://www.example.com/2", "null", null,
                        LocalDate.now(), LocalDate.now().plusDays(30L), 0)
        );

        when(urlRepository.findByUserId(userId)).thenReturn(mockUrls);
        when(userService.findByUsername(username)).thenReturn(userDto);
        when(urlMapper.toUrlDto(mockUrls.get(0))).thenReturn(mockUrlDtos.get(0));
        when(urlMapper.toUrlDto(mockUrls.get(1))).thenReturn(mockUrlDtos.get(1));


        List<UrlDto> result = urlService.getAllUrlUser(username);


        assertEquals(mockUrlDtos, result);
    }

    private UrlDto createUrlDto(Long id, String shortUrl, String url, String description, String username,
                                LocalDate createdDate, LocalDate expirationDate, int visitCount) {
        UrlDto urlDto = new UrlDto();
        urlDto.setId(id);
        urlDto.setShortUrl(shortUrl);
        urlDto.setUrl(url);
        urlDto.setDescription(description);
        urlDto.setUsername(username);
        urlDto.setCreatedDate(createdDate);
        urlDto.setExpirationDate(expirationDate);
        urlDto.setVisitCount(visitCount);
        return urlDto;
    }
}
