package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemCreationDtoTest {

    @Test
    void testItemCreationDto() {
        ItemCreationDto itemCreationDto = new ItemCreationDto(
                1L,
                1L,
                "item",
                "firstItem",
                true,
                null);

        assertEquals(itemCreationDto.hashCode(), Objects.hash(itemCreationDto.getId(),
                itemCreationDto.getOwnerId(),
                itemCreationDto.getName(),
                itemCreationDto.getDescription(),
                itemCreationDto.getAvailable(),
                itemCreationDto.getRequestId()));
    }
}