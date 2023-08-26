package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.List;

public interface BookingService {
    List<BookingDto> findBookingsForUser(long userId, String status);

    List<BookingDto> findBookingsForOwner(long ownerId, String status);

    BookingDto findBooking(long userId, long bookingId);

    BookingDto createBooking(long userId, BookingCreationDto bookingCreationDto);

    BookingDto updateBooking(long ownerId, long bookingId, boolean approved);

    ResponseFormat deleteBooking(long userId, long bookingId);
}