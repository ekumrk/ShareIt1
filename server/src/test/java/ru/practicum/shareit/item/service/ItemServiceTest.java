package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
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
class ItemServiceTest {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final EntityManager manager;

    InputItemDto inputItemDto = createInputItemDto("item1", "first_item");
    InputItemDto inputItemDto2 = createInputItemDto("item2", "second_item");

    InputItemRequestDto inputItemRequestDto = InputItemRequestDto
            .builder()
            .description("desc")
            .build();

    UserDto owner = createUserDto("owner", "first@email.ru");
    UserDto booker = createUserDto("booker", "second@email.ru");
    UserDto user1 = createUserDto("user1", "third@email.ru");

    CommentDto commentDto = createCommentDto("comment", booker.getName());
    CommentDto commentDtoFail = createCommentDto("comment", user1.getName());

    @BeforeEach
    void init() {
        owner = userService.addNewUser(owner);
        booker = userService.addNewUser(booker);
        user1 = userService.addNewUser(user1);

        inputItemDto = itemService.addNewItem(owner.getId(), inputItemDto);
    }

    @Test
    void addNewItemSuccessfully() {
        TypedQuery<Item> query = manager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", inputItemDto.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(inputItemDto.getId()));
        assertThat(item.getName(), equalTo(inputItemDto.getName()));
        assertThat(item.getDescription(), equalTo(inputItemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(inputItemDto.getAvailable()));

        inputItemRequestDto = itemRequestService.post(owner.getId(), inputItemRequestDto);
        inputItemDto2.setRequestId(inputItemRequestDto.getId());

        inputItemDto2 = itemService.addNewItem(owner.getId(), inputItemDto2);

        query = manager.createQuery("Select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", inputItemDto2.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(inputItemDto2.getId()));
        assertThat(item.getName(), equalTo(inputItemDto2.getName()));
        assertThat(item.getDescription(), equalTo(inputItemDto2.getDescription()));
        assertThat(item.getAvailable(), equalTo(inputItemDto2.getAvailable()));
    }

    @Test
    void shouldFailAddNewItem() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemService.addNewItem(-1L, inputItemDto2));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> itemService.addNewItem(100L, inputItemDto2));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        inputItemDto2.setRequestId(10L);

        e = assertThrows(EntityNotFoundException.class,
                () -> itemService.addNewItem(owner.getId(), inputItemDto2));
        assertThat(e.getMessage(), equalTo("Запрос не найден!"));
    }

    @Test
    void shouldUpdateItemSuccessfully() {
        inputItemDto.setName("updatedName");
        inputItemDto = itemService.updateItem(owner.getId(), inputItemDto.getId(), inputItemDto);

        TypedQuery<Item> query = manager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", inputItemDto.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(inputItemDto.getId()));
        assertThat(item.getName(), equalTo(inputItemDto.getName()));
        assertThat(item.getDescription(), equalTo(inputItemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(inputItemDto.getAvailable()));
    }

    @Test
    void shouldFailUpdateItemWithWrongId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(-1L, inputItemDto.getId(), inputItemDto));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(user1.getId(), inputItemDto.getId(), inputItemDto));
        assertThat(e.getMessage(), equalTo("Только владелец вещи может обновлять данные!"));
    }

    @Test
    void shouldGetItemSuccessfully() {
        generateNextAndLastBookings(inputItemDto.getId());
        ItemDto dto = itemService.getItem(owner.getId(), inputItemDto.getId());

        TypedQuery<Item> query = manager.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        Item item = query.setParameter("id", owner.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(dto.getId()));
        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getDescription(), equalTo(dto.getDescription()));
        assertThat(item.getAvailable(), equalTo(dto.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(owner.getId()));

        dto = itemService.getItem(user1.getId(), inputItemDto.getId());

        query = manager.createQuery("Select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", inputItemDto.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(dto.getId()));
        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getDescription(), equalTo(dto.getDescription()));
        assertThat(item.getAvailable(), equalTo(dto.getAvailable()));
    }

    @Test
    void shouldFailGetItemWithWrongId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItem(-1L, inputItemDto.getId()));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItem(owner.getId(), -1L));
        assertThat(e.getMessage(), equalTo("Вещь не найдена!"));
    }

    @Test
    void shouldGetItemsSuccessfully() {
        List<ItemDto> listDto = itemService.getItems(owner.getId(), 0, 10);

        TypedQuery<Item> query = manager.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        List<Item> list = query.setParameter("id", owner.getId()).getResultList();

        assertThat(listDto.get(0).getId(), equalTo(list.get(0).getId()));
    }

    @Test
    void shouldFailGetItems() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItems(-1L, 0, 10));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldFindByTextSuccessfully() {
        String text = "first";
        List<InputItemDto> tmp = itemService.findByText(owner.getId(), text, 0, 10);
        assertThat(tmp.get(0).getId(), equalTo(inputItemDto.getId()));
    }

    @Test
    void shouldFailFindByText() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemService.findByText(-1L, "text", 0, 10));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldAddCommentSuccessfully() {
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(1))
                .itemId(inputItemDto.getId())
                .build();
        BookingDto dto = bookingService.create(booker.getId(), inputBookingDto);
        bookingService.approve(owner.getId(), dto.getId(), true);
        commentDto = itemService.addComment(booker.getId(), inputItemDto.getId(), commentDto);

        TypedQuery<Comment> query = manager.createQuery("Select c from Comment c where c.author.id = :id", Comment.class);
        Comment comment = query.setParameter("id", booker.getId()).getSingleResult();

        assertThat(commentDto.getAuthorName(), equalTo(comment.getAuthor().getName()));
    }

    @Test
    void shouldFailAddComment() {
        Exception e = assertThrows(EntityValidationException.class,
                () -> itemService.addComment(user1.getId(), inputItemDto.getId(), commentDtoFail));
        assertThat(e.getMessage(), equalTo("Пользователь еще не арендовал данную вещь!"));
    }

    private InputItemDto createInputItemDto(String name, String description) {
        return InputItemDto.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }

    private UserDto createUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

    private CommentDto createCommentDto(String text, String authorName) {
        return CommentDto.builder()
                .text(text)
                .authorName(authorName)
                .created(LocalDateTime.now())
                .build();
    }

    private void generateNextAndLastBookings(Long itemId) {
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(1))
                .itemId(itemId)
                .build();
        BookingDto dto = bookingService.create(booker.getId(), inputBookingDto);
        bookingService.approve(owner.getId(), dto.getId(), true);

        inputBookingDto.setStart(LocalDateTime.now().plusNanos(1));
        inputBookingDto.setEnd(LocalDateTime.now().plusNanos(2));

        dto = bookingService.create(booker.getId(), inputBookingDto);
        bookingService.approve(owner.getId(), dto.getId(), true);

        inputBookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        inputBookingDto.setEnd(LocalDateTime.now().plusMinutes(2));

        dto = bookingService.create(booker.getId(), inputBookingDto);
        bookingService.approve(owner.getId(), dto.getId(), true);
    }
}