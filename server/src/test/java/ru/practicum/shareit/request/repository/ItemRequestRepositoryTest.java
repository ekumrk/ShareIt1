package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    private final Pageable page = PageRequest.of(Integer.parseInt(FROM) / Integer.parseInt(SIZE),
            Integer.parseInt(SIZE), Sort.by(Sort.Direction.ASC, "id"));

    private User owner;
    private User requestor;

    private Item item1;
    private Item item2;
    private Item item3;

    private ItemRequest request;
    private ItemRequest request2;

    @BeforeEach
    void init() {
        owner = createUser("owner", "owner@email.ru");
        requestor = createUser("requestor", "requestor@email.ru");

        item1 = createItem("УШМ", "Угловая шлифовальная машина", owner);
        item2 = createItem("Шуруповерт", "Аккумуляторная дрель-шуруповерт", owner);
        item3 = createItem("Кувалда", "Молоток особо крупного размера", owner);

        owner = userRepository.save(owner);
        requestor = userRepository.save(requestor);

        request = ItemRequest.builder()
                .requestor(requestor)
                .description("УШМ")
                .created(Instant.now())
                .build();
        requestRepository.save(request);

        request2 = ItemRequest.builder()
                .requestor(owner)
                .description("Перфоратор")
                .created(Instant.now())
                .build();
        requestRepository.save(request2);

        item1.setRequest(request);
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);
    }

    @AfterEach
    void clear() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> result = requestRepository.findAllByRequestorIdOrderByRequestorIdDesc(requestor.getId());

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), request.getId());
    }

    @Test
    void findAllByRequestorIdNot() {
        List<ItemRequest> result = requestRepository.findAllByRequestorIdNot(requestor.getId(), page);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), request2.getId());
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
}