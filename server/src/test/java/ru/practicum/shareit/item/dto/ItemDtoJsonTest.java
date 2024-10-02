package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws IOException {
        List<CommentDto> comments = List.of(new CommentDto(1L, "comment1 text", "author name1",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)),
                new CommentDto(2L, "comment2 text", "author name2",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        BookingInfoDto next = new BookingInfoDto(3L, 2L);
        BookingInfoDto last = new BookingInfoDto(1L, 2L);
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);
        item.setLastBooking(last);
        item.setNextBooking(next);
        item.setComments(comments);

        JsonContent<ItemDto> result = json.write(item);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathValue("$.lastBooking").extracting("bookerId")
                .isEqualTo(item.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathValue("$.nextBooking").extracting("bookerId")
                .isEqualTo(item.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);
    }

    @Test
    void testSerializeWithNull() throws Exception {
        List<CommentDto> comments = List.of(new CommentDto(1L, "comment1 text", "author name1",
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)),
                new CommentDto(2L, "comment2 text", "author name2",
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        BookingInfoDto last = new BookingInfoDto(1L, 2L);
        ItemDto item = new ItemDto();
        item.setName("item name");
        item.setLastBooking(last);
        item.setComments(comments);

        JsonContent<ItemDto> result = json.write(item);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
        assertThat(result).extractingJsonPathValue("$.lastBooking").extracting("bookerId")
                .isEqualTo(item.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);
    }
}
