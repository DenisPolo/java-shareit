package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationDto {

    private Long id;

    @NotNull
    @Future(message = "Время старта аренды не может быть в прошлом")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Время завершения аренды не может быть в прошлом")
    private LocalDateTime end;

    private Long bookerId;

    @NotNull
    private Long itemId;

    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingCreationDto that = (BookingCreationDto) o;
        return Objects.equals(id, that.id)
                && Objects.equals(start, that.start)
                && Objects.equals(end, that.end)
                && Objects.equals(bookerId, that.bookerId)
                && Objects.equals(itemId, that.itemId)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, bookerId, itemId, status);
    }
}