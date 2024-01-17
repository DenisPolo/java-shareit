package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestServiceImpl service;

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findUsersItemRequests(
            @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(service.findUsersItemRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                 @RequestParam(defaultValue = "0") int from,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.findItemRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> findItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PathVariable long requestId) {
        return ResponseEntity.ok().body(service.findItemRequest(userId, requestId));
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestBody ItemRequest itemRequest) {
        return ResponseEntity.ok().body(service.createItemRequest(userId, itemRequest));
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<ResponseFormat> deleteItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @PathVariable long requestId) {
        return ResponseEntity.ok().body(service.deleteItemRequest(userId, requestId));
    }
}