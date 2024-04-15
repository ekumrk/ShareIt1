package ru.yandex.practicum.ShareIt.item;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.user.User;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Item {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private User owner;
    private Boolean isAvailable;
    private ItemRequest request;
}
