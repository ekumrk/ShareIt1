package ru.practicum.shareit.booking.handler;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

public class StateCurrentHandler extends Handler {
    @Override
    public List<Booking> handleRequest(State state, BookingRepository repository, Long userId,
                                       Boolean owner, Pageable page, LocalDateTime now) {

        if (!state.equals(State.CURRENT) && next != null) {
            return next.handleRequest(state, repository, userId, owner, page, now);
        }

        if (owner) {
            return repository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now, page);
        } else {
            return repository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(userId, now, now, page);
        }
    }
}
