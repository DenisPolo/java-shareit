package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findItems(
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
            @PositiveOrZero(message = "\"from\" must be greater than or equal to zero")
            @RequestParam(defaultValue = "0") int from,
            @Positive(message = "\"size\" must be greater than zero") @RequestParam(defaultValue = "10") int size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.findItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long itemId) {
        log.info("Get item with userId={}, itemId={}", userId, itemId);
        return itemClient.findItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam(required = false) String text,
            @PositiveOrZero(message = "\"from\" must be greater than or equal to zero")
            @RequestParam(defaultValue = "0") int from,
            @Positive(message = "\"size\" must be greater than zero") @RequestParam(defaultValue = "10") int size) {
        log.info("Search items with text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @Valid @RequestBody ItemCreationDto itemCreationDto) {
        log.info("Save item with userId={}, itemName={}", userId, itemCreationDto.getName());
        return itemClient.saveItem(userId, itemCreationDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId,
                                              @Valid @RequestBody CommentCreationDto comment) {
        log.info("Post comment with userId={}, itemId={}, comment={}", userId, itemId, comment.getText());
        return itemClient.postComment(userId, itemId, comment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemCreationDto itemCreationDto) {
        log.info("Update item with userId={}, itemId={}, itemName={}", userId, itemId, itemCreationDto.getName());
        return itemClient.updateItem(userId, itemId, itemCreationDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId) {
        log.info("Delete item with userId={}, itemId={}", userId, itemId);
        return itemClient.deleteItem(userId, itemId);
    }
}