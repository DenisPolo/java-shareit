package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentDtoTest {

    @Test
    void testCommentDto() {
        CommentDto commentDto = new CommentDto(
                1L,
                "Author",
                "any text",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        );

        assertEquals(commentDto.hashCode(), Objects.hash(commentDto.getId(),
                commentDto.getAuthorName(),
                commentDto.getText()));
    }
}