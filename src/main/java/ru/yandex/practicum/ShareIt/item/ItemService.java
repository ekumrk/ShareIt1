package ru.yandex.practicum.ShareIt.item;

import ru.yandex.practicum.ShareIt.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    InputItemDto addNewItem(Long userId, InputItemDto dto);

    InputItemDto updateItem(Long userId, Long itemId, InputItemDto dto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    List<InputItemDto> findByText(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);
}
