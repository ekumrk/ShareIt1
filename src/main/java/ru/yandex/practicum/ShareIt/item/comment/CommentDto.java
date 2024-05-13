package ru.yandex.practicum.ShareIt.item.comment;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotNull
    private String text;
    private String authorName;
    private LocalDateTime created;
}
