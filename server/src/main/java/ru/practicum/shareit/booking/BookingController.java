package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @GetMapping
    public ResponseEntity<List<BookingDto>> findBookingsForUser(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.findBookingsForUser(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findBookingsForOwner(
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.findBookingsForOwner(ownerId, state, from, size));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findBooking(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        return ResponseEntity.ok().body(service.findBooking(userId, bookingId));
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody BookingCreationDto bookingCreationDto) {
        return ResponseEntity.ok().body(service.createBooking(userId, bookingCreationDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                    @PathVariable long bookingId, @RequestParam boolean approved) {
        return ResponseEntity.ok().body(service.updateBooking(ownerId, bookingId, approved));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ResponseFormat> deleteBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PathVariable long bookingId) {
        return ResponseEntity.ok().body(service.deleteBooking(userId, bookingId));
    }
}