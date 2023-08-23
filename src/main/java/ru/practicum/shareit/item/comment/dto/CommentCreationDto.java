package ru.practicum.shareit.item.comment.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentCreationDto {

    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
}