package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentCreationDtoTest {

    @Test
    void testCommentCreationDto() {
        CommentCreationDto commentCreationDto = new CommentCreationDto(
                "any text"
        );

        assertEquals(commentCreationDto.hashCode(), Objects.hash(commentCreationDto.getText()));
    }
}
