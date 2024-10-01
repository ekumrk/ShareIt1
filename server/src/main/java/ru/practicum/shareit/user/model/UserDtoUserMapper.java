package ru.practicum.shareit.user.model;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserDtoUserMapper {
    UserDto mapUserToUserDto(User user);

    User mapUserDtoToUser(UserDto userDto);
}
