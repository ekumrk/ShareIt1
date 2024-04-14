package ru.yandex.practicum.ShareIt.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
