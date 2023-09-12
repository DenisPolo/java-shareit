package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingForItemDtoTest {

    @Autowired
    private JacksonTester<BookingForItemDto> json;

    @Test
    void testBookingForItemDto() throws Exception {

        BookingForItemDto bookingForItemDto = new BookingForItemDto(
                1L,
                LocalDateTime.of(2023, 1, 3, 22, 0),
                LocalDateTime.of(2023, 1, 3, 22, 0),
                1L,
                BookingStatus.APPROVED,
                "2023.01.03 12:00:00"
        );

        assertEquals(bookingForItemDto.hashCode(), Objects.hash(bookingForItemDto.getId(),
                bookingForItemDto.getStart(),
                bookingForItemDto.getEnd(),
                bookingForItemDto.getBookerId(),
                bookingForItemDto.getStatus()));

        assertEquals(bookingForItemDto, new BookingForItemDto(
                1L,
                LocalDateTime.of(2023, 1, 3, 22, 0),
                LocalDateTime.of(2023, 1, 3, 22, 0),
                1L,
                BookingStatus.APPROVED,
                "2023.01.03 12:00:00"
        ));

        JsonContent<BookingForItemDto> result = json.write(bookingForItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-03T22:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-03T22:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.creationDate").isEqualTo("2023.01.03 12:00:00");
    }
}
