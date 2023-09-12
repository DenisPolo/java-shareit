package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCreationDtoTest {

    @Test
    void testUserCreationDto() {
        UserCreationDto userCreationDto = new UserCreationDto(
                "user@mail",
                "User"
        );

        assertEquals(userCreationDto.hashCode(), Objects.hash(userCreationDto.getEmail(),
                userCreationDto.getName()
        ));
    }
}