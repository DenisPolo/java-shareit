package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingsDtoTest {

    @Autowired
    private JacksonTester<ItemWithBookingsDto> json;

    @Test
    void testUserDto() throws Exception {
        BookingForItemDto booking = new BookingForItemDto(
                1L,
                LocalDateTime.of(2023, 1, 3, 22, 0),
                LocalDateTime.of(2023, 1, 4, 22, 0),
                1L,
                BookingStatus.APPROVED,
                "2023.01.03 12:00:00"
        );

        CommentDto comment = new CommentDto(
                1L,
                "Author",
                "comment_text",
                LocalDateTime.of(2023, 1, 5, 22, 0)
        );

        ItemWithBookingsDto item = new ItemWithBookingsDto(
                1L,
                "item",
                "firstItem",
                true,
                booking,
                null,
                List.of(comment),
                "2023.01.02 12:00:00"
        );

        JsonContent<ItemWithBookingsDto> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("firstItem");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-01-03T22:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2023-01-04T22:00:00");
        assertThat(result).extractingJsonPathValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment_text");
        assertThat(result).extractingJsonPathStringValue("$.creationDate").isEqualTo("2023.01.02 12:00:00");
    }
}
