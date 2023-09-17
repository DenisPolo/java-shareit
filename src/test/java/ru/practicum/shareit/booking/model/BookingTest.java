package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    @Test
    void testBooking() {

        User user = new User(
                1L,
                "user@mail",
                "User",
                LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        Item item = new Item(
                1L,
                user,
                "item",
                "firstItem",
                true,
                null,
                LocalDateTime.of(2023, 1, 2, 12, 0, 0));

        Booking booking = new Booking(
                1L,
                LocalDateTime.of(2023, 1, 3, 22, 0),
                LocalDateTime.of(2023, 1, 3, 22, 0),
                user,
                item,
                BookingStatus.APPROVED,
                LocalDateTime.of(2023, 1, 3, 12, 0, 0)
        );

        assertEquals(booking.hashCode(), Objects.hash(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStatus()));

        assertEquals(booking, new Booking(
                1L,
                LocalDateTime.of(2023, 1, 3, 22, 0),
                LocalDateTime.of(2023, 1, 3, 22, 0),
                user,
                item,
                BookingStatus.APPROVED,
                LocalDateTime.of(2023, 1, 3, 12, 0, 0)
        ));
    }
}