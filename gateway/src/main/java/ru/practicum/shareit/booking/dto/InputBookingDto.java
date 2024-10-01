package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
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
