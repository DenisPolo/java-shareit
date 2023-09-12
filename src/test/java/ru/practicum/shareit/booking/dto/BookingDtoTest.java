package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        UserDto user = new UserDto(
                1L,
                "user@mail",
                "User",
                "2023.01.01 12:00:00");

        ItemDto item = new ItemDto(
                1L,
                "item",
                "firstItem",
                true,
                "2023.01.02 12:00:00");

        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2023, 1, 3, 22, 0),
                LocalDateTime.of(2023, 1, 3, 22, 0),
                user,
                item,
                BookingStatus.APPROVED,
                "2023.01.03 12:00:00");

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-03T22:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-03T22:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.creationDate").isEqualTo("2023.01.03 12:00:00");
    }
}