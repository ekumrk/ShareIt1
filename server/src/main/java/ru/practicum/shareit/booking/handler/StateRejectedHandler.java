package ru.practicum.shareit.booking.handler;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

public class StateRejectedHandler extends Handler {
    @Override
    public List<Booking> handleRequest(State state, BookingRepository repository, Long userId,
                                       Boolean owner, Pageable page, LocalDateTime now) {

        if (!state.equals(State.REJECTED) && next != null) {
            return next.handleRequest(state, repository, userId, owner, page, now);
        }

        if (owner) {
            return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
        } else {
            return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
        }
    }
}
