package ru.yandex.practicum.ShareIt.item;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.ShareIt.request.ItemRequest;
import ru.yandex.practicum.ShareIt.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    private User owner;
    private Boolean isAvailable;
    private ItemRequest request;
}
