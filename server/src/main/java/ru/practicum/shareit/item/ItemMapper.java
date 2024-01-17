package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "creationDate", target = "creationDate", dateFormat = "yyyy.MM.dd hh:mm:ss")
    ItemDto mapToItemDto(Item item);

    List<ItemDto> mapToItemDto(Iterable<Item> items);

    default ItemWithBookingsDto mapToItemWithBookingsDto(Item item, List<Booking> itemBookings) {
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
        BookingForItemDto lastBooking = (last.isEmpty()) ? null : BookingMapper.INSTANCE
                .mapToBookingForItemDto(last.get());
        BookingForItemDto nextBooking = (next.isEmpty()) ? null : BookingMapper.INSTANCE
                .mapToBookingForItemDto(next.get());

        return new ItemWithBookingsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                lastBooking, nextBooking, new ArrayList<>(), creationDate);
    }

    @Mapping(source = "item.creationDate", target = "creationDate", dateFormat = "yyyy.MM.dd hh:mm:ss")
    @Mapping(source = "request.id", target = "requestId")
    ItemForItemRequestDto mapToItemForItemRequestDto(Item item);

    List<ItemForItemRequestDto> mapToItemForItemRequestDto(List<Item> items);

    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    @Mapping(source = "itemDto.description", target = "description")
    Item mapToNewItem(ItemCreationDto itemDto, User owner, ItemRequest request);
}