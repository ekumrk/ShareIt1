package ru.yandex.practicum.ShareIt.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ShareIt.booking.assistive.State;
import ru.yandex.practicum.ShareIt.booking.assistive.Status;
import ru.yandex.practicum.ShareIt.exception.EntityNotFoundException;
import ru.yandex.practicum.ShareIt.exception.EntityValidationException;
import ru.yandex.practicum.ShareIt.item.Item;
import ru.yandex.practicum.ShareIt.item.ItemRepository;
import ru.yandex.practicum.ShareIt.user.User;
import ru.yandex.practicum.ShareIt.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoBookingMapper mapper;

    @Override
    public BookingDto create(Long userId, InputBookingDto dto) {
        if (dto.getStart().equals(dto.getEnd()) || dto.getStart().isAfter(dto.getEnd())) {
            throw new EntityValidationException("Некорректные начало и конец!");
        }

        User booker = getUser(userId);
        Item item = getItem(dto.getItemId());

        if (!item.getIsAvailable()) {
            throw new EntityValidationException("Вещь недоступна к бронированию!");
        }

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Владелец не может бронировать собственную вещь!");
        }

        Booking booking = mapper.mapInputBookingDtoToBooking(dto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return mapper.mapBookingToBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        getUser(userId);
        Booking booking = getBooking(bookingId);

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new EntityValidationException("Бронь уже подтверждена!");
        }

        if (!booking.getItem().getIsAvailable()) {
            throw new EntityValidationException("Вещь недоступна к бронированию!");
        }

        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new EntityNotFoundException("Только владелец вещи может подтверждать бронь!");
        } else {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        }
        return mapper.mapBookingToBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto getBookingInfo(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);

        if (Objects.equals(booking.getBooker().getId(), userId) ||
                Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return mapper.mapBookingToBookingDto(booking);
        }

        throw new EntityNotFoundException("Просматривать могут только владелец вещи, либо арендатор!");
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        State.checkState(state);
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(userId,
                                now, now)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            default:
                throw new EntityValidationException("Непредвиденная ошибка! Обратитесь в службу поддержки!");
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        State.checkState(state);
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                now, now)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, now)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, now)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED)
                        .stream()
                        .map(mapper::mapBookingToBookingDto)
                        .collect(Collectors.toUnmodifiableList());

            default:
                throw new EntityValidationException("Непредвиденная ошибка! Обратитесь в службу поддержки!");
        }
    }

    @Override
    public BookingInfoDto getLastBooking(Long itemId) {
        Optional<Booking> tmp = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), Status.APPROVED);
        return tmp.map(mapper::mapBookingToBookingInfoDto).orElse(null);
    }

    @Override
    public BookingInfoDto getNextBooking(Long itemId) {
        Optional<Booking> tmp = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), Status.APPROVED);
        return tmp.map(mapper::mapBookingToBookingInfoDto).orElse(null);
    }

    @Override
    public List<BookingDto> getBookingByBooker(Long bookerId, Long itemId) {
        Optional<List<Booking>> tmp = bookingRepository.findByBookerIdAndItemIdAndEndBefore(bookerId,
                itemId, LocalDateTime.now());
        return tmp.map(mapper::mapBookingsToToBookingDtos).orElse(null);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден!")
        );
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Вещь не найдена!")
        );
    }

    private Booking getBooking(Long BookingId) {
        return bookingRepository.findById(BookingId).orElseThrow(
                () -> new EntityNotFoundException("Бронь не найдена!")
        );
    }
}
