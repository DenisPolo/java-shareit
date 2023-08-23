package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private ItemDto item;
    private BookingStatus status;
    private String creationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return id.equals(that.id)
                && start.equals(that.start)
                && end.equals(that.end)
                && booker.equals(that.booker)
                && item.equals(that.item)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, booker, item, status);
    }
}