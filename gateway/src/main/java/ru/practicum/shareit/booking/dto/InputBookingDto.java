package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class InputBookingDto {
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
