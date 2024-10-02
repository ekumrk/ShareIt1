package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws IOException {
        UserDto booker = new UserDto(1L, "John Wick", "john.wick@comiccon.com");
        InputItemDto item = new InputItemDto(1L, "item name", "item description", true, 1L);

        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        dto.setEnd(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        dto.setItem(item);
        dto.setBooker(booker);
        dto.setStatus(Status.WAITING);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(dto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(dto.getEnd().toString());
        assertThat(result).extractingJsonPathValue("$.item").extracting("name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathValue("$.booker").extracting("name").isEqualTo(booker.getName());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(dto.getStatus().toString());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        UserDto booker = new UserDto(1L, "John Wick", "john.wick@comiccon.com");

        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setBooker(booker);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNull();
        assertThat(result).extractingJsonPathValue("$.item").isNull();
        assertThat(result).extractingJsonPathValue("$.booker").extracting("name").isEqualTo(booker.getName());
        assertThat(result).extractingJsonPathValue("$.status").isNull();
    }
}
