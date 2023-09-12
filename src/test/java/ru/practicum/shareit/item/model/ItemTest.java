package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemTest {

    @Test
    void testUserDto() {
        User user = new User(
                1L,
                "user@mail",
                "User",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        Item item = new Item(
                1L,
                user,
                "item",
                "item description",
                true,
                null,
                LocalDateTime.of(2023, 1, 5, 22, 0)
        );

        assertEquals(item.hashCode(), Objects.hash(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription()
        ));
    }
}
