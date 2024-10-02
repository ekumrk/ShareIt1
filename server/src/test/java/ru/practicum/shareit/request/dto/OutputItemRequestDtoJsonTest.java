package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.InputItemDto;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OutputItemRequestDtoJsonTest {
    private final JacksonTester<OutputItemRequestDto> json;

    @Test
    void testSerialize() throws IOException {
        OutputItemRequestDto dto = new OutputItemRequestDto();
        List<InputItemDto> items = new ArrayList<>();
        InputItemDto itemDto = new InputItemDto(1L,"item name", "item description", true, 100L);
        items.add(itemDto);
        dto.setId(1L);
        dto.setDescription("request description");
        dto.setCreated(Instant.now());
        dto.setItems(items);

        JsonContent<OutputItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
    }

    @Test
    void testSerializeWithNull() throws Exception {
        OutputItemRequestDto dto = new OutputItemRequestDto();
        dto.setId(1L);
        dto.setDescription("request description");
        dto.setCreated(Instant.now());

        JsonContent<OutputItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);

        OutputItemRequestDto dto1 = new OutputItemRequestDto();
        dto1.setDescription("request description");
        dto1.setCreated(Instant.now());

        JsonContent<OutputItemRequestDto> result1 = json.write(dto1);

        assertThat(result1).hasJsonPath("$.id");
        assertThat(result1).hasJsonPath("$.description");
        assertThat(result1).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result1).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result1).extractingJsonPathStringValue("$.description").isEqualTo(dto1.getDescription());
        assertThat(result1).extractingJsonPathStringValue("$.created").isEqualTo(dto1.getCreated().toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }
}
