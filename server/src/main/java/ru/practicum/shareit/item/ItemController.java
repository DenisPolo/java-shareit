package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping
    public ResponseEntity<List<ItemWithBookingsDto>> findItems(
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.findItems(userId, from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingsDto> findItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PathVariable long itemId) {
        return ResponseEntity.ok().body(service.findItem(userId, itemId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(required = false) String text,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.searchItems(text, from, size));
    }

    @PostMapping
    public ResponseEntity<ItemForItemRequestDto> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestBody ItemCreationDto itemCreationDto) {
        return ResponseEntity.ok().body(service.saveItem(userId, itemCreationDto));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> postComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long itemId,
                                                  @RequestBody CommentCreationDto comment) {
        return ResponseEntity.ok().body(service.postComment(userId, itemId, comment));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId,
                                              @RequestBody ItemCreationDto itemCreationDto) {
        return ResponseEntity.ok().body(service.updateItem(userId, itemId, itemCreationDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ResponseFormat> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long itemId) {
        return ResponseEntity.ok().body(service.deleteItem(userId, itemId));
    }
}