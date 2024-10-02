package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(1L, "John Wick", "john.wick@comiccon.com");
        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
    }

    @Test
    void testSerializeWithNull() throws Exception {
        UserDto dto = new UserDto(null, "John Wick", "john.wick@comiccon.com");
        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());

        UserDto dto1 = new UserDto();
        dto1.setName("John Wick");
        dto1.setEmail("john.wick@comiccon.com");
        JsonContent<UserDto> result1 = json.write(dto1);

        assertThat(result1).hasJsonPath("$.id");
        assertThat(result1).hasJsonPath("$.name");
        assertThat(result1).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.id").isNull();
        assertThat(result1).extractingJsonPathStringValue("$.name").isEqualTo(dto1.getName());
        assertThat(result1).extractingJsonPathStringValue("$.email").isEqualTo(dto1.getEmail());

        UserDto dto2 = new UserDto();
        dto2.setName("John Wick");
        JsonContent<UserDto> result2 = json.write(dto2);

        assertThat(result2).hasJsonPath("$.id");
        assertThat(result2).hasJsonPath("$.name");
        assertThat(result2).hasJsonPath("$.email");
        assertThat(result2).extractingJsonPathStringValue("$.id").isNull();
        assertThat(result2).extractingJsonPathStringValue("$.name").isEqualTo(dto2.getName());
        assertThat(result2).extractingJsonPathStringValue("$.email").isNull();
    }
}
