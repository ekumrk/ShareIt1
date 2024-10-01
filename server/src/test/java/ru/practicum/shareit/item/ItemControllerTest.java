package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemService service;

    UserDto ownerDto = new UserDto(1L, "owner", "owner@email.ru");
    UserDto bookerDto = new UserDto(2L, "booker", "booker@email.ru");
    CommentDto commentDto = new CommentDto(1L, "some comment", bookerDto.getName(), LocalDateTime.now());

    ItemDto itemDto = new ItemDto(1L, "жидкие гвозди", "Клей эпоксидный, особо устойчивый",
            true, null, null, List.of(commentDto));

    InputItemDto inputItemDto = new InputItemDto(1L, "жидкие гвозди", "Клей эпоксидный, особо устойчивый",
            true, null);
    InputItemDto updateInputItemDto = new InputItemDto(1L, "жидкие гвозди update", "Клей эпоксидный, особо устойчивый",
            true, null);

    @Test
    void shouldAddSuccessfully() throws Exception {
        Mockito
                .when(service.addNewItem(anyLong(), any(InputItemDto.class)))
                .thenReturn(inputItemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID, ownerDto.getId())
                        .content(mapper.writeValueAsString(inputItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(inputItemDto)));

        verify(service, times(1)).addNewItem(anyLong(),
                any(InputItemDto.class));
    }

    @Test
    void shouldUpdateItemSuccessfully() throws Exception {
        Mockito
                .when(service.updateItem(anyLong(), anyLong(), any(InputItemDto.class)))
                .thenReturn(updateInputItemDto);

        mockMvc.perform(patch("/items/{itemId}", updateInputItemDto.getId())
                        .header(USER_ID, ownerDto.getId())
                        .content(mapper.writeValueAsString(inputItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updateInputItemDto)));

        verify(service, times(1)).updateItem(anyLong(), anyLong(),
                any(InputItemDto.class));
    }

    @Test
    void shouldGetItemSuccessfully() throws Exception {
        Mockito
                .when(service.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(USER_ID, ownerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(service, times(1)).getItem(anyLong(), anyLong());
    }

    @Test
    void shouldGetSuccessfully() throws Exception {
        Mockito
                .when(service.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID, ownerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(service, times(1)).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldFailGetNegativeUserId() throws Exception {
        Mockito
                .when(service.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items}")
                        .header(USER_ID, -1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldSearchSuccessfully() throws Exception {
        Mockito
                .when(service.findByText(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(inputItemDto));

        mockMvc.perform(get("/items/search?text={text}", "Гвозди")
                        .header(USER_ID, ownerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(inputItemDto))));

        verify(service, times(1)).findByText(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldAddCommentSuccessfully() throws Exception {
        Mockito
                .when(service.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", inputItemDto.getId())
                        .header(USER_ID, bookerDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(service, times(1)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}