package ru.yandex.practicum.ShareIt.item;

import static ru.yandex.practicum.ShareIt.constants.Constants.USER_ID;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ShareIt.item.comment.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
        private final ItemService itemService;

        @PostMapping
        public InputItemDto add(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                @RequestBody @Valid InputItemDto dto) {
            return itemService.addNewItem(userId, dto);
        }

        @PatchMapping("/{itemId}")
        public InputItemDto updateItem(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                       @PathVariable @NotNull @Positive Long itemId,
                                       @RequestBody InputItemDto dto) {
            return itemService.updateItem(userId, itemId, dto);
        }

        @GetMapping("/{itemId}")
        public ItemDto getItem(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                               @PathVariable @NotNull @Positive Long itemId) {
            return itemService.getItem(userId, itemId);
        }

        @GetMapping
        public List<ItemDto> get(@RequestHeader(USER_ID) @NotNull @Positive Long userId) {
            return itemService.getItems(userId);
        }

        @GetMapping("/search")
        public List<InputItemDto> search(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                         @RequestParam String text) {
            return itemService.findByText(userId, text);
        }

        @PostMapping("/{itemId}/comment")
        public CommentDto addComment(@RequestHeader(USER_ID) @NotNull @Positive Long userId,
                                     @PathVariable @NotNull @Positive Long itemId,
                                     @RequestBody @Valid CommentDto comment) {
            return itemService.addComment(userId, itemId, comment);
        }
}