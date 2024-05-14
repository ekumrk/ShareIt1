package ru.yandex.practicum.ShareIt.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoUserMapper {
    UserDto mapUserToUserDto(User user);

    User mapUserDtoToUser(UserDto userDto);
}
