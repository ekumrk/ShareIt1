package ru.practicum.shareit.request.model;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    InputItemRequestDto itemRequestToInputItemRequestDto(ItemRequest request);

    OutputItemRequestDto itemRequestToOutputItemRequestDto(ItemRequest request);

    ItemRequest inputItemRequestDtoToItemRequest(InputItemRequestDto dto);

    List<OutputItemRequestDto> itemRequestToOutputItemRequestDtos(List<ItemRequest> request);
}
