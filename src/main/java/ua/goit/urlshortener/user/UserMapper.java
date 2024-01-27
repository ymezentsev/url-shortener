package ua.goit.urlshortener.user;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toUserDto(UserEntity entity){
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }

    public UserEntity toUserEntity(UserDto dto){
        return UserEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }

    public List<UserDto> toUserDtoList(List<UserEntity> userEntities){
        return userEntities.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
}
