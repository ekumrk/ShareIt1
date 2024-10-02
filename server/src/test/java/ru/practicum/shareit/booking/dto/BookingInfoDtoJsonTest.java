package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingInfoDtoJsonTest {
    private final JacksonTester<BookingInfoDto> json;

    @Test
    void testSerialize() throws IOException {
        BookingInfoDto dto = new BookingInfoDto();
        dto.setId(1L);
        dto.setBookerId(1L);
        JsonContent<BookingInfoDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(dto.getBookerId().intValue());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        BookingInfoDto dto = new BookingInfoDto();
        dto.setBookerId(1L);
        JsonContent<BookingInfoDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(dto.getBookerId().intValue());
    }
}
