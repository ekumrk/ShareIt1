package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class BookingInfoDto {
    private Long id;
    private Long bookerId;
}
