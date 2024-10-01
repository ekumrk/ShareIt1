package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private User owner;
    private User booker;

    private Item item;

    private Comment comment;

    @BeforeEach
    void init() {
        owner = createUser("owner", "owner@email.ru");
        booker = createUser("requestor", "requestor@email.ru");

        item = createItem("УШМ", "Угловая шлифовальная машина", owner);

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        item = itemRepository.save(item);

        comment = Comment.builder()
                .item(item)
                .created(LocalDateTime.now())
                .author(booker)
                .text("Надежный инструмент!")
                .build();

        comment = commentRepository.save(comment);
    }

    @AfterEach
    void clear() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByItemId() {
        List<Comment> result = commentRepository.findAllByItemId(item.getId());

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), comment.getId());
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