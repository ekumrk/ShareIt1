package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager manager;


    UserDto owner = createUserDto("owner", "owner@email.ru");
    UserDto booker = createUserDto("booker", "booker@email.ru");
    UserDto requestor = createUserDto("requestor", "requestor@email.ru");

    InputItemRequestDto inputItemRequestDto = createInputItemRequestDto("гвоздь");
    InputItemRequestDto inputItemRequestDto1 = createInputItemRequestDto("шуруп");

    @BeforeEach
    void init() {
        owner = userService.addNewUser(owner);
        booker = userService.addNewUser(booker);
        requestor = userService.addNewUser(requestor);

        InputItemDto inputItemDto = createInputItemDto();
        inputItemDto = itemService.addNewItem(owner.getId(), inputItemDto);

        inputItemRequestDto = itemRequestService.post(requestor.getId(), inputItemRequestDto);
        inputItemRequestDto1 = itemRequestService.post(owner.getId(), inputItemRequestDto1);
    }

    @Test
    void shouldPostRequestSuccessfully() {
        TypedQuery<ItemRequest> query = manager
                .createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", inputItemRequestDto.getId()).getSingleResult();

        assertThat(request.getId(), equalTo(inputItemRequestDto.getId()));
        assertThat(request.getRequestor().getId(), equalTo(requestor.getId()));
        assertThat(request.getDescription(), equalTo(inputItemRequestDto.getDescription()));
        assertThat(request.getCreated(), equalTo(inputItemRequestDto.getCreated()));
    }

    @Test
    void shouldFailPostRequestWithWrongId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.post(-1L, inputItemRequestDto1));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetOwnRequestsSuccessfully() {
        TypedQuery<ItemRequest> query = manager
                .createQuery("Select i from ItemRequest i where i.requestor.id = :id", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("id", requestor.getId()).getResultList();

        List<OutputItemRequestDto> tmp = itemRequestService.getOwnRequests(requestor.getId());

        assertThat(requests.get(0).getDescription(), equalTo(tmp.get(0).getDescription()));
    }

    @Test
    void shouldFailGetOwnRequestsWithWrongId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getOwnRequests(-1L));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetUsersRequestsSuccessfully() {
        TypedQuery<ItemRequest> query = manager
                .createQuery("Select i from ItemRequest i where i.requestor.id != :id", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("id", requestor.getId()).getResultList();

        List<OutputItemRequestDto> list = itemRequestService.getUsersRequests(requestor.getId(), 0, 10);

        assertThat(list.get(0).getDescription(), equalTo(requests.get(0).getDescription()));
    }

    @Test
    void shouldFailGetUsersRequestsWithWrongId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getUsersRequests(-1L, 0, 10));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetRequestSuccessfully() {
        TypedQuery<ItemRequest> query = manager
                .createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", inputItemRequestDto.getId()).getSingleResult();

        OutputItemRequestDto req = itemRequestService.getRequest(owner.getId(), inputItemRequestDto.getId());

        assertThat(request.getDescription(), equalTo(req.getDescription()));
    }

    @Test
    void shouldFailGetRequestWithWrongId() {
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getRequest(-1L, inputItemRequestDto.getId()));
        assertThat(e.getMessage(), equalTo("Пользователь не найден!"));

        e = assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getRequest(owner.getId(), -1L));
        assertThat(e.getMessage(), equalTo("Запрос не найден!"));
    }

    private InputItemDto createInputItemDto() {
        return InputItemDto.builder()
                .name("item1")
                .description("first_item")
                .available(true)
                .build();
    }

    private InputItemRequestDto createInputItemRequestDto(String desc) {
        return InputItemRequestDto.builder()
                .description(desc)
                .build();
    }

    private UserDto createUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}