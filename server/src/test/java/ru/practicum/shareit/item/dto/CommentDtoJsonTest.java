package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws IOException {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("comment text");
        dto.setAuthorName("author name");
        dto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("@.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setText("comment text");
        dto.setAuthorName("author name");
        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("@.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();
    }
}