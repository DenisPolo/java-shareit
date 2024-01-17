package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findUsersItemRequests(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("Get itemRequests with userId={}", userId);
        return itemRequestClient.findUsersItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero(message = "\"from\" must be greater than or equal to zero")
            @RequestParam(defaultValue = "0") int from,
            @Positive(message = "\"size\" must be greater than zero") @RequestParam(defaultValue = "10") int size) {
        log.info("Get all itemRequests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long requestId) {
        log.info("Get itemRequest with userId={}, requestId={}", userId, requestId);
        return itemRequestClient.findItemRequest(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody ItemRequestCreationDto itemRequest) {
        log.info("Create itemRequest with userId={}, requestDescription={}", userId, itemRequest.getDescription());
        return itemRequestClient.createItemRequest(userId, itemRequest);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> deleteItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long requestId) {
        log.info("Delete itemRequest with userId={}, requestId={}", userId, requestId);
        return itemRequestClient.deleteItemRequest(userId, requestId);
    }
}