package ru.yandex.practicum.ShareIt.booking;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.ShareIt.booking.assistive.Status;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
class InputBookingDto {
    @Positive
    private Long itemId;
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @FutureOrPresent
    @NotNull
    private LocalDateTime end;
    private Status status;
}
