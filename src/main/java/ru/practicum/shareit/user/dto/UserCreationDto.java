package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserCreationDto {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email не соответстует формату адреса электронной почты")
    private String email;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
}