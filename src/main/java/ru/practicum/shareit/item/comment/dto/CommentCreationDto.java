package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentCreationDto {

    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
}