package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InputBookingDtoJsonTest {
    private final JacksonTester<InputBookingDto> json;

    @Test
    void testSerialize() throws IOException {
        InputBookingDto dto = new InputBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        dto.setEnd(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        dto.setStatus(Status.WAITING);

        JsonContent<InputBookingDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(dto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(dto.getEnd().toString());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(dto.getStatus().toString());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        InputBookingDto dto = new InputBookingDto();
        dto.setItemId(1L);

        JsonContent<InputBookingDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNull();
        assertThat(result).extractingJsonPathValue("$.status").isNull();
    }
}
