package ru.yandex.practicum.ShareIt.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
}
