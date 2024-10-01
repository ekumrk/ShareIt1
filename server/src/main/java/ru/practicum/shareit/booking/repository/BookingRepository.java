package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long id, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(Long id,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, Status status, Pageable page);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end,
                                                                                    Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime end, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime start, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long id, Status status, Pageable page);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId,
                                                                             LocalDateTime start, Status status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId,
                                                                             LocalDateTime start, Status status);

    Optional<List<Booking>> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);
}
