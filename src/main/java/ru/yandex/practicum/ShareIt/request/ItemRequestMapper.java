package ru.yandex.practicum.ShareIt.request;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequest itemRequest) {
        return ItemRequest.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequest(ItemRequestDto dto) {
        return ItemRequestDto.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(dto.getRequestor())
                .created(dto.getCreated())
                .build();
    }
}
