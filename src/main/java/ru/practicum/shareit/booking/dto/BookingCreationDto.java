package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
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
}