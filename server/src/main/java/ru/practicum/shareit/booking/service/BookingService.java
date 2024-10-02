package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, InputBookingDto dto);

    BookingDto approve(Long userId, Long bookingId, boolean approved);

    BookingDto getBookingInfo(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getBookingsByOwner(Long userId, String state, Integer from, Integer size);

    BookingInfoDto getNextBooking(Long itemId);

    BookingInfoDto getLastBooking(Long itemId);

    List<BookingDto> getBookingByBooker(Long bookerId, Long itemId);
}