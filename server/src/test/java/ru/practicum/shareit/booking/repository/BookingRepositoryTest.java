package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    private final Pageable page = PageRequest.of(Integer.parseInt(FROM) / Integer.parseInt(SIZE),
            Integer.parseInt(SIZE), Sort.by(Sort.Direction.ASC, "id"));

    private final LocalDateTime past = LocalDateTime.now().plusSeconds(10);
    private final LocalDateTime now = LocalDateTime.now().plusMinutes(1);
    private final LocalDateTime future = LocalDateTime.now().plusMinutes(2);

    private User owner;
    private User booker;
    private User user;

    private Item item1;
    private Item item2;

    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;

    @BeforeEach
    void init() {
        owner = createUser("owner", "owner@email.ru");
        booker = createUser("booker", "booker@email.ru");
        user = createUser("user", "user@email.ru");

        item1 = createItem("УШМ", "Угловая шлифовальная машина", owner);
        item2 = createItem("Шуруповерт", "Аккумуляторная дрель-шуруповерт", owner);

        pastBooking = createBooking(past.minusSeconds(1), past.plusSeconds(2), item1, booker);
        currentBooking = createBooking(now.minusSeconds(1), now.plusSeconds(2), item1, booker);
        futureBooking = createBooking(future.minusSeconds(1), future.plusSeconds(2), item2, user);

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        user = userRepository.save(user);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        pastBooking = bookingRepository.save(pastBooking);
        currentBooking = bookingRepository.save(currentBooking);
        futureBooking = bookingRepository.save(futureBooking);
    }

    @AfterEach
    void clear() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllBookingsByBookerId() {
        List<Booking> result = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), page);

        assertEquals(result.size(), 2);
        assertEquals(result.get(1).getId(), pastBooking.getId());
        assertEquals(result.get(0).getId(), currentBooking.getId());

        result = bookingRepository.findAllByBookerIdOrderByStartDesc(5000L, page);

        assertEquals(result.size(), 0);
    }

    @Test
    void findCurrentBookingsByBookerId() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(booker.getId(), now, now, page);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), currentBooking.getId());
        assertEquals(result.get(0).getItem().getName(), item1.getName());
        assertEquals(result.get(0).getBooker().getId(), booker.getId());

        result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(5000L, now, now, page);

        assertEquals(result.size(), 0);
    }

    @Test
    void findPastBookingsByBookerId() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(booker.getId(), now.minusSeconds(10), page);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), pastBooking.getId());
    }

    @Test
    void findFutureBookingsByBookerId() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsAfterOrderByStartDesc(user.getId(), now.plusSeconds(10), page);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), futureBooking.getId());
    }

    @Test
    void findBookingsByBookerIdAndByStatusWaiting() {
        currentBooking.setStatus(Status.REJECTED);
        currentBooking = bookingRepository.save(currentBooking);

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), pastBooking.getId());
    }

    @Test
    void findBookingsByBookerIdAndByStatusRejected() {
        futureBooking.setStatus(Status.WAITING);
        futureBooking = bookingRepository.save(futureBooking);

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED, page);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerId() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), page);

        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getId(), futureBooking.getId());

        result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(booker.getId(), page);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCurrentBookingsByOwnerId() {
        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner.getId(), now, now, page);

        assertEquals(result.get(0).getId(), currentBooking.getId());
    }

    @Test
    void findPastBookingsByOwnerId() {
        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(owner.getId(), now.minusSeconds(10), page);

        assertEquals(result.get(0).getId(), pastBooking.getId());
    }

    @Test
    void findFutureBookingsByOwnerId() {
        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(owner.getId(), now.plusSeconds(10), page);

        assertEquals(result.get(0).getId(), futureBooking.getId());
    }

    @Test
    void findBookingsByOwnerIdAndStatusWaiting() {
        currentBooking.setStatus(Status.REJECTED);
        currentBooking = bookingRepository.save(currentBooking);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING, page);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), futureBooking.getId());
    }

    @Test
    void findAllByOwnerIdAndStatusRejected() {
        futureBooking.setStatus(Status.REJECTED);
        futureBooking = bookingRepository.save(futureBooking);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED, page);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), futureBooking.getId());
    }

    @Test
    void shouldGetNextBookingByItemId() {
        currentBooking.setStatus(Status.APPROVED);
        currentBooking = bookingRepository.save(currentBooking);

        Optional<Booking> result = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item1.getId(), now.minusSeconds(10), Status.APPROVED);

        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), currentBooking.getId());
    }

    @Test
    void shouldGetLastBookingByItemId() {
        pastBooking.setStatus(Status.APPROVED);
        pastBooking = bookingRepository.save(pastBooking);

        Optional<Booking> result = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item1.getId(), now, Status.APPROVED);

        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), pastBooking.getId());
    }

    @Test
    void getBookingByBookerIdAndItemId() {
        Optional<List<Booking>> result = bookingRepository
                .findByBookerIdAndItemIdAndEndBefore(booker.getId(), item1.getId(), now);

        assertTrue(result.isPresent());
        assertEquals(result.get().get(0).getId(), pastBooking.getId());
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private Item createItem(String name, String desc, User owner) {
        return Item.builder()
                .name(name)
                .description(desc)
                .available(true)
                .owner(owner)
                .build();
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, Item item, User booker) {
        return Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }
}