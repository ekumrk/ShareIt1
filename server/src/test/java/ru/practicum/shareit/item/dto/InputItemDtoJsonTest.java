package ru.practicum.shareit.item.dto;

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
public class InputItemDtoJsonTest {
    private final JacksonTester<InputItemDto> json;

    @Test
    void testSerialize() throws IOException {
        InputItemDto item = new InputItemDto();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);
        item.setRequestId(1L);

        JsonContent<InputItemDto> result = json.write(item);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(item.getRequestId().intValue());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        InputItemDto item = new InputItemDto();
        item.setName("item name");

        JsonContent<InputItemDto> result = json.write(item);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();
    }
}
