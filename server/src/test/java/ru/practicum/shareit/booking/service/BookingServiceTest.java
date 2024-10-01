package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager manager;

    UserDto ownerDto = createUserDto("owner", "owner@email.ru");
    UserDto bookerDto = createUserDto("booker", "booker@email.ru");

    InputItemDto inputItemDto = createInputItemDto("жидкие гвозди", "Клей эпоксидный, особо устойчивый");
    InputItemDto inputItemDto1 = createInputItemDto("УШМ", "Угловая шлифовальная машина");


    InputBookingDto inputBookingDto;
    BookingDto bookingDto;

    @BeforeEach
    void init() {
        ownerDto = userService.addNewUser(ownerDto);
        bookerDto = userService.addNewUser(bookerDto);

        inputItemDto = itemService.addNewItem(ownerDto.getId(), inputItemDto);
        inputItemDto1 = itemService.addNewItem(ownerDto.getId(), inputItemDto1);

        inputBookingDto = createInputBookingDto(inputItemDto.getId());
        bookingDto = bookingService.create(bookerDto.getId(), inputBookingDto);
    }

    @Test
    void shouldCreateSuccessfully() {
        TypedQuery<Booking> query = manager.createQuery("Select b from Booking b where b.item.id = :id", Booking.class);
        Booking booking = query.setParameter("id", inputItemDto.getId()).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingDto.getId()));
        assertThat(booking.getBooker().getId(), equalTo(bookerDto.getId()));
        assertThat(booking.getItem().getId(), equalTo(inputItemDto.getId()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerDto.getId()));
    }

    @Test
    void shouldFailCreateWithWrongBookerIdOrWrongFields() {
        InputBookingDto dto = createInputBookingDto(inputItemDto1.getId());

        Exception e = assertThrows(EntityNotFoundException.class, () -> bookingService.create(-1L, dto));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class, () -> bookingService.create(ownerDto.getId(), dto));
        assertThat(e.getMessage(), equalTo("Владелец не может бронировать собственную вещь!"));

        e = assertThrows(EntityNotFoundException.class, () -> bookingService.create(100L, dto));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        inputItemDto1.setAvailable(false);
        itemService.updateItem(ownerDto.getId(), inputItemDto1.getId(), inputItemDto1);

        e = assertThrows(EntityValidationException.class, () -> bookingService.create(bookerDto.getId(), dto));
        assertThat(e.getMessage(), equalTo("Вещь недоступна к бронированию!"));

        inputBookingDto.setItemId(100L);
        e = assertThrows(EntityNotFoundException.class, () -> bookingService.create(ownerDto.getId(), inputBookingDto));
        assertThat(e.getMessage(), equalTo("Вещь не найдена!"));
    }

    @Test
    void shouldApproveSuccessfully() {
        bookingService.approve(ownerDto.getId(), bookingDto.getId(), false);

        TypedQuery<Booking> query = manager.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.getId()).getSingleResult();

        assertThat(booking.getStatus(), equalTo(Status.REJECTED));

        bookingService.approve(ownerDto.getId(), bookingDto.getId(), true);

        query = manager.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        booking = query.setParameter("id", bookingDto.getId()).getSingleResult();

        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void shouldFailApproveWithWrongOwnerIdOrItemUnavailable() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approve(bookerDto.getId(), bookingDto.getId(), true));
        assertThat(e.getMessage(), equalTo("Только владелец вещи может подтверждать бронь!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approve(-1L, bookingDto.getId(), true));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approve(100L, bookingDto.getId(), true));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        inputItemDto.setAvailable(false);
        itemService.updateItem(ownerDto.getId(), inputItemDto.getId(), inputItemDto);

        e = assertThrows(EntityValidationException.class,
                () -> bookingService.approve(ownerDto.getId(), bookingDto.getId(), true));
        assertThat(e.getMessage(), equalTo("Вещь недоступна к бронированию!"));

        inputItemDto.setAvailable(true);
        itemService.updateItem(ownerDto.getId(), inputItemDto.getId(), inputItemDto);
        bookingService.approve(ownerDto.getId(), bookingDto.getId(), true);

        e = assertThrows(EntityValidationException.class,
                () -> bookingService.approve(ownerDto.getId(), bookingDto.getId(), true));
        assertThat(e.getMessage(), equalTo("Бронь уже подтверждена!"));
    }

    @Test
    void shouldGetBookingInfoSuccessfully() {
        BookingDto dto = bookingService.getBookingInfo(ownerDto.getId(), bookingDto.getId());

        TypedQuery<Booking> query = manager.createQuery("Select b from Booking b where b.item.owner.id = :id", Booking.class);
        Booking bookingByOwner = query.setParameter("id", ownerDto.getId()).getSingleResult();

        assertThat(bookingByOwner.getItem().getOwner().getId(), equalTo(ownerDto.getId()));
        assertThat(bookingByOwner.getItem().getId(), equalTo(dto.getItem().getId()));

        dto = bookingService.getBookingInfo(bookerDto.getId(), bookingDto.getId());

        query = manager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        Booking bookingByBooker = query.setParameter("id", bookerDto.getId()).getSingleResult();

        assertThat(bookingByBooker.getBooker().getId(), equalTo(bookerDto.getId()));
        assertThat(bookingByBooker.getItem().getId(), equalTo(dto.getItem().getId()));
    }

    @Test
    void shouldFailGetBookingInfoWithWrongUserId() {
        UserDto user = createUserDto("user", "user@email.ru");
        UserDto lambdaUser = userService.addNewUser(user);

        Exception e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(lambdaUser.getId(), bookingDto.getId()));
        assertThat(e.getMessage(), equalTo("Просматривать могут только владелец вещи, либо арендатор!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(-1L, bookingDto.getId()));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(lambdaUser.getId(), -1L));
        assertThat(e.getMessage(), equalTo("Бронь не найдена!"));
    }

    @Test
    void shouldGetUserBookingsSuccessfully() {
        List<BookingDto> list = bookingService.getUserBookings(bookerDto.getId(), "ALL", 0, 10);

        TypedQuery<Booking> query = manager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        List<Booking> bookingsByBooker = query.setParameter("id", bookerDto.getId()).getResultList();

        assertThat(bookingsByBooker.get(0).getBooker().getId(), equalTo(list.get(0).getBooker().getId()));

        list = bookingService.getUserBookings(bookerDto.getId(), "FUTURE", 0, 10);

        query = manager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        bookingsByBooker = query.setParameter("id", bookerDto.getId()).getResultList();

        assertThat(bookingsByBooker.get(0).getBooker().getId(), equalTo(list.get(0).getBooker().getId()));

        list = bookingService.getUserBookings(bookerDto.getId(), "WAITING", 0, 10);

        query = manager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        bookingsByBooker = query.setParameter("id", bookerDto.getId()).getResultList();

        assertThat(bookingsByBooker.get(0).getBooker().getId(), equalTo(list.get(0).getBooker().getId()));

        List<State> states = List.of(State.CURRENT, State.PAST, State.REJECTED);
        for (State state : states) {
            list = bookingService.getUserBookings(bookerDto.getId(), String.valueOf(state), 0, 10);
            assertThat(list.size(), equalTo(0));
        }
    }

    @Test
    void shouldFailGetUserBookingsWithWrongUserId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(-1L, bookingDto.getId()));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetBookingsByOwnerSuccessfully() {
        List<BookingDto> list = bookingService.getBookingsByOwner(ownerDto.getId(), "ALL", 0, 10);

        TypedQuery<Booking> query = manager.createQuery("Select b from Booking b where b.item.owner.id = :id", Booking.class);
        List<Booking> bookingsByOwner = query.setParameter("id", ownerDto.getId()).getResultList();

        assertThat(list.get(0).getBooker().getId(), equalTo(bookingsByOwner.get(0).getBooker().getId()));

        list = bookingService.getBookingsByOwner(ownerDto.getId(), "FUTURE", 0, 10);

        query = manager.createQuery("Select b from Booking b where b.item.owner.id = :id", Booking.class);
        bookingsByOwner = query.setParameter("id", ownerDto.getId()).getResultList();

        assertThat(bookingsByOwner.get(0).getBooker().getId(), equalTo(list.get(0).getBooker().getId()));

        list = bookingService.getBookingsByOwner(ownerDto.getId(), "WAITING", 0, 10);

        query = manager.createQuery("Select b from Booking b where b.item.owner.id = :id", Booking.class);
        bookingsByOwner = query.setParameter("id", ownerDto.getId()).getResultList();

        assertThat(bookingsByOwner.get(0).getBooker().getId(), equalTo(list.get(0).getBooker().getId()));

        List<State> states = List.of(State.CURRENT, State.PAST, State.REJECTED);
        for (State state : states) {
            list = bookingService.getBookingsByOwner(ownerDto.getId(), String.valueOf(state), 0, 10);
            assertThat(list.size(), equalTo(0));
        }
    }

    @Test
    void shouldFailGetBookingsByOwnerWithWrongUserId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(-1L, bookingDto.getId()));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    private UserDto createUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

    private InputItemDto createInputItemDto(String name, String description) {
        return InputItemDto.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }

    private InputBookingDto createInputBookingDto(Long itemId) {
        return InputBookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(30))
                .end(LocalDateTime.now().plusHours(1))
                .itemId(itemId)
                .build();
    }
}