package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemCreationDto {

    private Long id;

    private Long ownerId;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull
    private Boolean available;
}