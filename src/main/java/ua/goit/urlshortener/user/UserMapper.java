package ua.goit.urlshortener.user;

import org.springframework.stereotype.Component;
import ua.goit.urlshortener.user.UserEntity;
import ua.goit.urlshortener.user.UserDto;

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
}
