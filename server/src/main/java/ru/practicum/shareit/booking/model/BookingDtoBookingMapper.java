package ru.practicum.shareit.booking.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingDtoBookingMapper {
    BookingDto mapBookingToBookingDto(Booking booking);

    List<BookingDto> mapBookingsToToBookingDtos(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingInfoDto mapBookingToBookingInfoDto(Booking booking);

    Booking mapInputBookingDtoToBooking(InputBookingDto inputBooking);
}
