package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    void testUser() {
        User user = new User(
                1L,
                "user@mail",
                "User",
                LocalDateTime.of(2023, 1, 5, 22, 0)
        );

        assertEquals(user.hashCode(), Objects.hash(user.getId(),
                user.getEmail(),
                user.getName()
        ));
    }
}