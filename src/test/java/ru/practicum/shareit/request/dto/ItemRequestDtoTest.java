package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testUserDto() throws Exception {
        ItemForItemRequestDto item = new ItemForItemRequestDto(
                1L,
                "item",
                "firstItem",
                true,
                1L,
                "2023.01.02 12:00:00"
        );

        ItemRequestDto request = new ItemRequestDto(
                1L,
                "any item description",
                "2023.01.03 12:00:00",
                List.of(item)
        );

        assertEquals(request.hashCode(), Objects.hash(request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getItems()
        ));

        JsonContent<ItemRequestDto> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("any item description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023.01.03 12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("firstItem");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.items[0].creationDate").isEqualTo("2023.01.02 12:00:00");
    }
}