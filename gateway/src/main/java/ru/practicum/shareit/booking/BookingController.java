package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.EntityValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID) @Positive long userId,
                                           @RequestBody @Valid InputBookingDto dto) {
        if (dto.getStart().equals(dto.getEnd()) || dto.getStart().isAfter(dto.getEnd())) {
            throw new EntityValidationException("Некорректные начало и конец!");
        }
        log.info("Gateway. Creating booking {}, userId={}", dto, userId);
        return bookingClient.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID) @Positive long userId,
                                                 @PathVariable @Positive long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Gateway. Approving bookingId={}, userId={}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) @Positive long userId,
                                             @PathVariable @Positive long bookingId) {
        log.info("Gateway. Get booking by id={}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(USER_ID) @Positive long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = FROM) @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = SIZE) @Positive int size) {
        State.checkState(state);
        log.info("Gateway. Get bookings by owner with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) @Positive long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = FROM) @PositiveOrZero int from,
                                              @Positive @RequestParam(name = "size", defaultValue = SIZE) @Positive int size) {
        State.checkState(state);
        log.info("Gateway. Get bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }
}
