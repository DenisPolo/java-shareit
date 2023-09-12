package ru.practicum.shareit.item.comment.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    @Test
    void testComment() {
        User user = new User(
                1L,
                "user@mail",
                "User",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        Comment comment = new Comment(
                1L,
                user,
                1L,
                "any text",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        );

        assertEquals(comment.hashCode(), Objects.hash(comment.getId(),
                comment.getAuthor(),
                comment.getItemId(),
                comment.getText()));

        assertEquals(comment, new Comment(
                1L,
                user,
                1L,
                "any text",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        ));
    }
}