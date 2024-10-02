package ru.practicum.shareit.item.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Mapper(componentModel = "spring")
public interface ItemDtoItemMapper {
    ItemDto mapItemToItemDto(Item item);

    @Mapping(target = "requestId", source = "request.id")
    InputItemDto mapItemToInputItemDto(Item item);

    Item mapInputItemDtoToItem(InputItemDto dto);
}
