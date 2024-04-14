package ru.yandex.practicum.ShareIt.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ShareIt.exception.EntityNotFoundException;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userIdToUser = new HashMap<>();
    private final Set<String> userEmail = new HashSet<>();
    private Long id = 1L;

    @Override
    public User addUser(UserDto dto) {
        checkIfEmailAlreadyExists(dto.getEmail());
        dto.setId(generateId());
        if (!userIdToUser.containsKey(dto.getId())) {
            userIdToUser.put(dto.getId(), UserMapper.toUser(dto));
            userEmail.add(dto.getEmail());
        } else {
            throw new ValidationException("Пользователь уже существует!");
        }
        return getUserById(dto.getId());
    }

    @Override
    public User updateUser(UserDto dto) {
        checkIfUserExists(dto.getId());
        checkIfEmailAlreadyExists(dto.getEmail(), dto.getId());
        final User user = getUserById(dto.getId());
        if (dto.getName() == null) {
            dto.setName(user.getName());
        }
        if (dto.getEmail() == null) {
            dto.setEmail(user.getEmail());
        } else {
            userEmail.remove(user.getEmail());
            userEmail.add(dto.getEmail());
        }
        userIdToUser.put(dto.getId(), UserMapper.toUser(dto));
        return getUserById(dto.getId());
    }

    @Override
    public User getUserById(Long id) {
        checkIfUserExists(id);
        return userIdToUser.get(id);
    }

    @Override
    public List<User> getUsers() {
        return List.copyOf(userIdToUser.values());
    }

    @Override
    public void deleteUserById(Long id) {
        checkIfUserExists(id);
        User user = userIdToUser.remove(id);
        userEmail.remove(user.getEmail());
    }

    private void checkIfUserExists(Long id) {
        if (!userIdToUser.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь не найден!");
        }
    }

    private void checkIfEmailAlreadyExists(String email) {
        if (userEmail.contains(email)) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
    }

    private void checkIfEmailAlreadyExists(String email, Long userId) {
        if (userIdToUser.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(userId))) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
    }

    private Long generateId() {
        return id++;
    }
}
