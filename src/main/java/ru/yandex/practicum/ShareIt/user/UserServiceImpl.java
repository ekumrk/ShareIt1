package ru.yandex.practicum.ShareIt.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ShareIt.exception.EntityNotFoundException;
import ru.yandex.practicum.ShareIt.exception.EntityValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserDtoUserMapper mapper;

    @Override
    public UserDto addNewUser(UserDto dto) {
        if (dto.getId() != null) {
            throw new EntityValidationException("Новый пользователь не может иметь ID!");
        }

        User user = mapper.mapUserDtoToUser(dto);
        return mapper.mapUserToUserDto(repository.saveAndFlush(user));
    }

    @Override
    public UserDto updateUser(UserDto dto) {
        User user = getUser(dto.getId());

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        return mapper.mapUserToUserDto(repository.saveAndFlush(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.mapUserToUserDto(getUser(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(mapper::mapUserToUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteUserById(Long id) {
        getUser(id);
        repository.deleteById(id);
        repository.flush();
    }

    private User getUser(Long userId) {
        return repository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден!")
        );
    }
}