package ua.goit.urlshortener.url;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.goit.urlshortener.user.UserEntity;
import ua.goit.urlshortener.user.UserMapper;
import ua.goit.urlshortener.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UrlMapper {
    private final UserService userService;
    private final UserMapper userMapper;

    public List<UrlDto> toUrlDtos(Collection<UrlEntity> entities) {
        return entities.stream()
                .map(this::toUrlDto)
                .collect(Collectors.toList());
    }

    public UrlDto toUrlDto(UrlEntity entity) {
        UrlDto dto = new UrlDto();
        dto.setId(entity.getId());
        dto.setShortUrl(entity.getShortUrl());
        dto.setUrl(entity.getUrl());
        dto.setDescription(entity.getDescription());
        dto.setUsername(entity.getUser().getUsername());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setVisitCount(entity.getVisitCount());
        return dto;
    }

    public UrlEntity toUrlEntity(UrlDto dto) {
        UrlEntity entity = new UrlEntity();

        entity.setId(dto.getId());
        entity.setShortUrl(dto.getShortUrl());
        entity.setUrl(dto.getUrl());
        entity.setDescription(dto.getDescription());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setVisitCount(dto.getVisitCount());

        UserEntity user = userMapper.toUserEntity(userService.findByUsername(dto.getUsername()));
        entity.setUser(user);

        return entity;
    }
}