package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        String creationDate = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(booking.getCreationDate());

        UserDto booker = UserMapper.mapToUserDto(booking.getBooker());

        ItemDto item = ItemMapper.mapToItemDto(booking.getItem());

        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booker, item, booking.getStatus(),
                creationDate);
    }

    public static List<BookingDto> mapToBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(mapToBookingDto(booking));
        }

        return result;
    }

    public static Booking mapToNewBooking(BookingCreationDto bookingDto, User booker, Item item) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), booker, item, bookingDto.getStatus());
    }

    public static BookingForItemDto mapToBookingForItemDto(Booking booking) {
        String creationDate = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(booking.getCreationDate());

        Long booker = booking.getBooker().getId();

        return new BookingForItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booker,
                booking.getStatus(), creationDate);
    }
}