package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    UserDto requesterDto = new UserDto(1L, "requester", "requester@email.ru");
    UserDto bookerDto = new UserDto(2L, "booker", "booker@email.ru");

    InputItemRequestDto inputItemRequestDto1 = createInputItemRequestDto(1L, "гвоздь");

    OutputItemRequestDto outputItemRequestDto1 = createOutputItemRequestDto(requesterDto.getId(), "гвоздь");

    @Test
    void shouldPostRequestSuccessfully() throws Exception {
        Mockito
                .when(service.post(anyLong(), any(InputItemRequestDto.class)))
                .thenReturn(inputItemRequestDto1);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, requesterDto.getId())
                        .content(mapper.writeValueAsString(inputItemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(inputItemRequestDto1)));

        verify(service, times(1)).post(anyLong(), any(InputItemRequestDto.class));
    }

    @Test
    void shouldGetOwnRequestsSuccessfully() throws Exception {
        Mockito
                .when(service.getOwnRequests(anyLong()))
                .thenReturn(List.of(outputItemRequestDto1));

        mockMvc.perform(get("/requests")
                        .header(USER_ID, requesterDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(inputItemRequestDto1))));

        verify(service, times(1)).getOwnRequests(anyLong());
    }

    @Test
    void shouldGetUsersRequestsSuccessfully() throws Exception {
        Mockito
                .when(service.getUsersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(outputItemRequestDto1));

        mockMvc.perform(get("/requests/all?from={from}&size={size}", 0, 10)
                        .header(USER_ID, bookerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(inputItemRequestDto1))));

        verify(service, times(1)).getUsersRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldGetRequestSuccessfully() throws Exception {
        Mockito
                .when(service.getRequest(anyLong(), anyLong()))
                .thenReturn(outputItemRequestDto1);

        mockMvc.perform(get("/requests/{requestId}", inputItemRequestDto1.getId())
                        .header(USER_ID, requesterDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(inputItemRequestDto1)));

        verify(service, times(1)).getRequest(anyLong(), anyLong());
    }

    private InputItemRequestDto createInputItemRequestDto(Long id, String desc) {
        return InputItemRequestDto.builder()
                .id(id)
                .description(desc)
                .build();
    }

    private OutputItemRequestDto createOutputItemRequestDto(Long id, String desc) {
        return OutputItemRequestDto.builder()
                .id(id)
                .description(desc)
                .items(new ArrayList<>())
                .build();
    }
}