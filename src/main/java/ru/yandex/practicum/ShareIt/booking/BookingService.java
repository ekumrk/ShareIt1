package ru.yandex.practicum.ShareIt.booking;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, InputBookingDto dto);

    BookingDto approve(Long userId, Long bookingId, boolean approved);

    BookingDto getBookingInfo(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long userId, String state);

    BookingInfoDto getNextBooking(Long itemId);

    BookingInfoDto getLastBooking(Long itemId);

    List<BookingDto> getBookingByBooker(Long bookerId, Long itemId);
}
