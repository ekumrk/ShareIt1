package ru.yandex.practicum.ShareIt.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingDtoBookingMapper {
    BookingDto mapBookingToBookingDto(Booking booking);

    List<BookingDto> mapBookingsToToBookingDtos(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingInfoDto mapBookingToBookingInfoDto(Booking booking);

    Booking mapInputBookingDtoToBooking(InputBookingDto inputBooking);
}
