package ru.yandex.practicum.ShareIt.user;

import java.util.List;

public interface UserRepository {
    User addUser(UserDto user);

    User updateUser(UserDto user);

    User getUserById(Long id);

    List<User> getUsers();

    void deleteUserById(Long id);
}
