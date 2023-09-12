package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemForItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemForItemRequestDto> json;

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

        JsonContent<ItemForItemRequestDto> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("firstItem");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.creationDate").isEqualTo("2023.01.02 12:00:00");
    }
}
