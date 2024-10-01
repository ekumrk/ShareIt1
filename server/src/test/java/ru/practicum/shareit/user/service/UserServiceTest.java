package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService service;
    private final EntityManager manager;

    UserDto userDto1 = createUserDto("userDto1", "userDto1@email.ru");
    UserDto userDto2 = createUserDto("userDto2", "userDto2@email.ru");

    @BeforeEach
    void init() {
        userDto1 = service.addNewUser(userDto1);
        userDto2 = service.addNewUser(userDto2);
    }

    @Test
    void shouldAddUserSuccessfully() {
        TypedQuery<User> query = manager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto1.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto1.getId()));
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        userDto1.setName("update");
        userDto1 = service.updateUser(userDto1);

        TypedQuery<User> query = manager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto1.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto1.getId()));
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void shouldFailUpdateUser() throws Exception {
        userDto1.setId(20L);

        Exception e = assertThrows(EntityNotFoundException.class,
                () -> service.updateUser(userDto1));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        userDto2 = service.getUserById(userDto1.getId());

        TypedQuery<User> query = manager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto1.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto2.getId()));
        assertThat(user.getName(), equalTo(userDto2.getName()));
        assertThat(user.getEmail(), equalTo(userDto2.getEmail()));
    }

    @Test
    void shouldFailGetUserById() throws Exception {
        userDto1.setId(20L);

        Exception e = assertThrows(EntityNotFoundException.class,
                () -> service.getUserById(userDto1.getId()));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetUsersSuccessfully() {
        List<UserDto> list = service.getUsers();

        TypedQuery<User> query = manager.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(list.size(), equalTo(users.size()));
    }


    @Test
    void shouldDeleteUserByIdSuccessfully() {
        service.deleteUserById(userDto2.getId());

        List<UserDto> list = service.getUsers();

        TypedQuery<User> query = manager.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(list.size(), equalTo(1));
        assertThat(users.size(), equalTo(1));
    }

    @Test
    void shouldFailDeleteUserById() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> service.getUserById(100L));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    private UserDto createUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}