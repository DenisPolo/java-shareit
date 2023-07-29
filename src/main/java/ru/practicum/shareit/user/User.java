package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    private Long id;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email не соответстует формату адреса электронной почты")
    private String email;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
}