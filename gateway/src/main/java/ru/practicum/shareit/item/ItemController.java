package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({OnCreate.class})
    public ResponseEntity<Object> add(@RequestHeader(USER_ID) @Positive long userId,
                                      @RequestBody @Valid InputItemDto dto) {
        log.info("Gateway. Adding new item.");
        return itemClient.addNewItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) @Positive long userId,
                                             @PathVariable @Positive long itemId,
                                             @RequestBody InputItemDto dto) {
        log.info("Gateway. Updating item with id={}.", itemId);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) @Positive long userId,
                                          @PathVariable @Positive long itemId) {
        log.info("Gateway. Get item with id={}.", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) @Positive long userId,
                                      @RequestParam(defaultValue = FROM) @PositiveOrZero int from,
                                      @RequestParam(defaultValue = SIZE) @Positive int size) {
        log.info("Gateway. Get items with userId={}.", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID) @Positive long userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = FROM) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = SIZE) @Positive int size) {
        log.info("Gateway. Searching item containing text={}.", text);
        return itemClient.findByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) @Positive long userId,
                                             @PathVariable @Positive long itemId,
                                             @RequestBody @Valid CommentDto comment) {
        log.info("Gateway. Adding comment to item with id={}.", itemId);
        return itemClient.addComment(userId, itemId, comment);
    }
}
