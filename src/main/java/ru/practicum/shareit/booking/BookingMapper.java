package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(uses = {UserMapper.class})
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "creationDate", target = "creationDate", dateFormat = "yyyy.MM.dd hh:mm:ss")
    BookingDto mapToBookingDto(Booking booking);

    List<BookingDto> mapToBookingDto(Iterable<Booking> bookings);

    @Mapping(source = "bookingDto.id", target = "id")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "item", target = "item")
    Booking mapToNewBooking(BookingCreationDto bookingDto, User booker, Item item);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingForItemDto mapToBookingForItemDto(Booking booking);
}