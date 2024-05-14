package ru.yandex.practicum.ShareIt.item;

import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.user.User;

public class ItemMapper {
    public static Item toItem(ItemDto dto, User user, ItemRequest request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .owner(user)
                .isAvailable(dto.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }
}
