package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @MockBean
    private UserService service;

    UserDto user1 = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    @Test
    void shouldAddUserSuccessfully() throws Exception {
        Mockito
                .when(service.addNewUser(any(UserDto.class)))
                .thenReturn(user1);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));

        verify(service, times(1)).addNewUser(any(UserDto.class));
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        user1.setName("update");

        Mockito
                .when(service.updateUser(any(UserDto.class)))
                .thenReturn(user1);

        mockMvc.perform(patch("/users/{userId}", user1.getId())
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));

        verify(service, times(1)).updateUser(any(UserDto.class));
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        Mockito
                .when(service.getUserById(anyLong()))
                .thenReturn(user1);

        mockMvc.perform(get("/users/{userId}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user1)));

        verify(service, times(1)).getUserById(anyLong());
    }

    @Test
    void shouldGetUsersSuccessfully() throws Exception {
        Mockito
                .when(service.getUsers())
                .thenReturn(List.of(user1));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1))));

        verify(service, times(1)).getUsers();
    }
}