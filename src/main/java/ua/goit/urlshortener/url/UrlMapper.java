package ua.goit.urlshortener.url;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.goit.urlshortener.user.UserMapper;
import ua.goit.urlshortener.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UrlMapper {
    private final UserService userService;
    private final UserMapper userMapper;

    public List<UrlDto> toUrlDtoList(Collection<UrlEntity> entities) {
        return entities.stream()
                .map(this::toUrlDto)
                .collect(Collectors.toList());
    }

    public UrlDto toUrlDto(UrlEntity entity) {
        return UrlDto.builder()
                .id(entity.getId())
                .shortUrl(entity.getShortUrl())
                .url(entity.getUrl())
                .description(entity.getDescription())
                .username(entity.getUser().getUsername())
                .createdDate(entity.getCreatedDate())
                .expirationDate(entity.getExpirationDate())
                .visitCount(entity.getVisitCount())
                .build();
    }

    public UrlEntity toUrlEntity(UrlDto urlDto) {
        return UrlEntity.builder()
                .id(urlDto.getId())
                .shortUrl(urlDto.getShortUrl())
                .url(urlDto.getUrl())
                .description(urlDto.getDescription())
                .user(userMapper.toUserEntity(userService.findByUsername(urlDto.getUsername())))
                .createdDate(urlDto.getCreatedDate())
                .expirationDate(urlDto.getExpirationDate())
                .visitCount(urlDto.getVisitCount())
                .build();
    }
}