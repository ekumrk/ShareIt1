package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Handler {
    protected Handler next;

    public void setNextHandler(Handler next) {
        this.next = next;
    }

    public abstract List<Booking> handleRequest(State state, BookingRepository repository, Long userId,
                                                Boolean owner, Pageable page, LocalDateTime now);
}
