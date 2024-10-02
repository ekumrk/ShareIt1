package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public InputItemDto add(@RequestHeader(USER_ID) Long userId,
                            @RequestBody InputItemDto dto) {
        log.info("Server. Adding new item.");
        return itemService.addNewItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public InputItemDto updateItem(@RequestHeader(USER_ID) Long userId,
                                   @PathVariable Long itemId,
                                   @RequestBody InputItemDto dto) {
        log.info("Server. Updating item with id={}.", itemId);
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_ID) Long userId,
                           @PathVariable Long itemId) {
        log.info("Server. Get item with id={}.", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader(USER_ID) Long userId,
                             @RequestParam(defaultValue = FROM) Integer from,
                             @RequestParam(defaultValue = SIZE) Integer size) {
        log.info("Server. Get items with userId={}.", userId);
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<InputItemDto> search(@RequestHeader(USER_ID) Long userId,
                                     @RequestParam String text,
                                     @RequestParam(defaultValue = FROM) Integer from,
                                     @RequestParam(defaultValue = SIZE) Integer size) {
        log.info("Server. Searching item containing text={}.", text);
        return itemService.findByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto comment) {
        log.info("Server. Adding comment to item with id={}.", itemId);
        return itemService.addComment(userId, itemId, comment);
    }
}