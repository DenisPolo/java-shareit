package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findBookingsForUser(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String stateParam,
            @PositiveOrZero(message = "'from' must be greater than or equal to zero")
            @RequestParam(defaultValue = "0") int from,
            @Positive(message = "'size' must be greater than zero") @RequestParam(defaultValue = "10") int size) {
        QueryState state = QueryState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingsForUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsForOwner(
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String stateParam,
            @PositiveOrZero(message = "'from' must be greater than or equal to zero")
            @RequestParam(defaultValue = "0") int from,
            @Positive(message = "'size' must be greater than zero") @RequestParam(defaultValue = "10") int size) {
        QueryState state = QueryState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.findBookingsForOwner(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                              @PathVariable long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody BookingCreationDto bookingCreationDto) {
        log.info("Create booking userId={}, ownerId={}, itemId={}, status={}", userId,
                bookingCreationDto.getBookerId(), bookingCreationDto.getItemId(), bookingCreationDto.getStatus());
        return bookingClient.createBooking(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @PathVariable long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Update booking ownerId={}, bookingId={}, approved={}", ownerId, bookingId, approved);
        return bookingClient.updateBooking(ownerId, bookingId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> deleteBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId) {
        return bookingClient.deleteBooking(userId, bookingId);
    }
}