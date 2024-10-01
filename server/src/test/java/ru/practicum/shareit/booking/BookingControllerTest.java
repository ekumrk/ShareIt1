package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;

    UserDto ownerDto = new UserDto(1L, "owner", "owner@email.ru");
    UserDto bookerDto = new UserDto(2L, "booker", "booker@email.ru");

    InputItemDto inputItemDto = new InputItemDto(1L, "жидкие гвозди", "Клей эпоксидный, особо устойчивый",
            true, null);
    InputBookingDto inputBookingDto = new InputBookingDto(1L,
            LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1), Status.WAITING);

    BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1L),
            inputItemDto, bookerDto, Status.WAITING);
    BookingDto bookingDtoApproved = new BookingDto(1L, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1L),
            inputItemDto, bookerDto, Status.APPROVED);

    @Test
    void shouldCreateSuccessfully() throws Exception {
        Mockito
                .when(bookingService.create(anyLong(), any(InputBookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, bookerDto.getId())
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1)).create(anyLong(),
                any(InputBookingDto.class));
    }

    @Test
    void shouldApproveSuccessfullyByOwner() throws Exception {
        Mockito
                .when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoApproved);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingDto.getId(), true)
                        .header(USER_ID, ownerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoApproved)));

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldApproveUnsuccessfullyByBooker() throws Exception {
        Mockito
                .when(bookingService.approve(eq(bookerDto.getId()), anyLong(), anyBoolean()))
                .thenThrow(new EntityNotFoundException("Только владелец вещи может подтверждать бронь!"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingDto.getId(), true)
                        .header(USER_ID, bookerDto.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("Только владелец вещи может подтверждать бронь!",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldGetBookingInfoSuccessfully() throws Exception {
        Mockito
                .when(bookingService.getBookingInfo(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, bookerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1)).getBookingInfo(anyLong(), anyLong());
    }

    @Test
    void shouldGetBookingInfoUnsuccessfullyWhenNotOwnerAndNotBooker() throws Exception {
        Mockito
                .when(bookingService.getBookingInfo(eq(10L), anyLong()))
                .thenThrow(new EntityNotFoundException("Просматривать могут только владелец вещи, либо арендатор!"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, 10L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("Просматривать могут только владелец вещи, либо арендатор!",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUserBookingsSuccessfully() throws Exception {
        Mockito
                .when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, bookerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1)).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
        verify(bookingService, times(0)).getBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getBookingsByOwnerSuccessfully() throws Exception {
        Mockito
                .when(bookingService.getBookingsByOwner(eq(ownerDto.getId()), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, ownerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1)).getBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt());
        verify(bookingService, times(0)).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }
}
