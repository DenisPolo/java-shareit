package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        String creationDate = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(item.getCreationDate());

        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), creationDate);
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            if (item.getAvailable()) {
                result.add(mapToItemDto(item));
            }
        }

        return result;
    }

    public static ItemWithBookingsDto mapToItemWithBookingsDto(Item item, List<Booking> itemBookings) {
        String creationDate = DateTimeFormatter
                .ofPattern("yyyy.MM.dd hh:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(item.getCreationDate());

        Optional<Booking> last = (itemBookings == null) ? Optional.empty() : itemBookings
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED) && b.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart));
        Optional<Booking> next = (itemBookings == null) ? Optional.empty() : itemBookings
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED) && b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart));
        BookingForItemDto lastBooking = (last.isEmpty()) ? null : BookingMapper.mapToBookingForItemDto(last.get());
        BookingForItemDto nextBooking = (next.isEmpty()) ? null : BookingMapper.mapToBookingForItemDto(next.get());

        return new ItemWithBookingsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                lastBooking, nextBooking, new ArrayList<>(), creationDate);
    }

    public static Item mapToNewItem(ItemCreationDto itemDto, User owner) {
        return new Item(owner, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}