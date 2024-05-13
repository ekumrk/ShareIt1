package ru.yandex.practicum.ShareIt.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class BookingInfoDto {
    private Long id;
    private Long bookerId;
}
