package ru.practicum.shareit.item.comment.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    @Test
    void testComment() {
        User owner = new User(
                1L,
                "owner@mail",
                "Owner",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        Item item = new Item(null, owner, "item1", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        User user = new User(
                1L,
                "user@mail",
                "User",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        Comment comment = new Comment(
                1L,
                user,
                item,
                "any text",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        );

        assertEquals(comment.hashCode(), Objects.hash(comment.getId(),
                comment.getAuthor(),
                comment.getItem(),
                comment.getText()));

        assertEquals(comment, new Comment(
                1L,
                user,
                item,
                "any text",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0)
        ));
    }
}