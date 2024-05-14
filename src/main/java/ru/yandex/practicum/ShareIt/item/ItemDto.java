package ru.yandex.practicum.ShareIt.item;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.ShareIt.booking.BookingInfoDto;
import ru.yandex.practicum.ShareIt.item.comment.CommentDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    private Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;
}