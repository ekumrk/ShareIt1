package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InputItemRequestDtoJsonTest {
    private final JacksonTester<InputItemRequestDto> json;

    @Test
    void testSerialize() throws IOException {
        InputItemRequestDto dto = new InputItemRequestDto();
        dto.setId(1L);
        dto.setDescription("some description");
        dto.setCreated(Instant.now());
        JsonContent<InputItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        InputItemRequestDto dto = new InputItemRequestDto();
        dto.setDescription("some description");
        dto.setCreated(Instant.now());
        JsonContent<InputItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());

        InputItemRequestDto dto1 = new InputItemRequestDto();
        dto.setDescription("some description2");
        JsonContent<InputItemRequestDto> result1 = json.write(dto1);

        assertThat(result1).hasJsonPath("$.id");
        assertThat(result1).hasJsonPath("$.description");
        assertThat(result1).hasJsonPath("$.created");
        assertThat(result1).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result1).extractingJsonPathStringValue("$.description").isEqualTo(dto1.getDescription());
        assertThat(result1).extractingJsonPathStringValue("$.created").isNull();
    }
}
