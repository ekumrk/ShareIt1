package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputItemRequestDto {
    @Null(groups = OnCreate.class)
    private Long id;
    @NotBlank
    private String description;
    private Instant created = Instant.now();
}
