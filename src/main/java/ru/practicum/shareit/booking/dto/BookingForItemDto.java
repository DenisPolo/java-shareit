package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class BookingForItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private BookingStatus status;
    private String creationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingForItemDto that = (BookingForItemDto) o;
        return id.equals(that.id)
                && start.equals(that.start)
                && end.equals(that.end)
                && bookerId.equals(that.bookerId)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, bookerId, status);
    }
}