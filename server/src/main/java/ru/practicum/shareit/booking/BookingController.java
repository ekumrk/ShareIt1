package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) Long userId,
                             @RequestBody InputBookingDto dto) {
        log.info("Server. Creating booking {}, userId={}", dto, userId);
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID) Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        log.info("Server. Approving bookingId={}, userId={}", bookingId, userId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfo(@RequestHeader(USER_ID) Long userId,
                                     @PathVariable Long bookingId) {
        log.info("Server. Get booking by id={}, userId={}", bookingId, userId);
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(USER_ID) Long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = FROM) Integer from,
                                            @RequestParam(defaultValue = SIZE) Integer size) {
        log.info("Server. Get bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(USER_ID) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = FROM) Integer from,
                                               @RequestParam(defaultValue = SIZE) Integer size) {
        log.info("Server. Get bookings by owner with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingService.getBookingsByOwner(userId, state, from, size);
    }
}
