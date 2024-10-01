package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({OnCreate.class})
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto dto) {
        log.info("Gateway. Adding new user.");
        return userClient.addNewUser(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive long userId, @RequestBody UserDto dto) {
        log.info("Gateway. Updating userId={}.", userId);
        dto.setId(userId);
        return userClient.updateUser(dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive long userId) {
        log.info("Gateway. Searching by id={}.", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Gateway. Get all users.");
        return userClient.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable @Positive long userId) {
        log.info("Gateway. Deleting by id={}.", userId);
        userClient.deleteUserById(userId);
    }
}
