package ru.yandex.practicum.ShareIt.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemDtoItemMapper {
    ItemDto mapItemToItemDto(Item item);

    InputItemDto mapItemToInputItemDto(Item item);

    Item mapInputItemDtoToItem(InputItemDto dto);
}
