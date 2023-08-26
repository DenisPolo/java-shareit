package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
    private String creationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemWithBookingsDto that = (ItemWithBookingsDto) o;
        return id.equals(that.id)
                && name.equals(that.name)
                && description.equals(that.description)
                && available.equals(that.available)
                && lastBooking.equals(that.lastBooking)
                && nextBooking.equals(that.nextBooking)
                && comments.equals(that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, lastBooking, nextBooking, comments);
    }
}