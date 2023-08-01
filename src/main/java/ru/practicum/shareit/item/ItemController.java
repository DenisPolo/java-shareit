package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping()
    public ResponseEntity findItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId) {
        return ResponseEntity.ok().body(service.findItems(userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity findItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return ResponseEntity.ok().body(service.findItem(itemId));
    }

    @GetMapping("/search")
    public ResponseEntity searchItems(
            @RequestParam(required = false) String text) {
        if (text == null) {
            String message = "Отсутствует параметр запроса";
            log.info(message);
            throw new RuntimeException(message);
        }
        if (text.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
        return ResponseEntity.ok().body(service.searchItems(text));
    }

    @PostMapping()
    public ResponseEntity saveItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody Item item) {
        item.setOwnerId(userId);
        return ResponseEntity.ok().body(service.saveItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                     @RequestBody Item item) {
        if ((item.getId() != null) && (item.getId() != itemId)) {
            String message = "В запросе не совпадают itemId в body и URI:\nPathVariable itemId: " + itemId +
                    "\nbody itemId: " + item.getId();
            log.info(message);
            throw new RuntimeException();
        }
        item.setId(itemId);
        return ResponseEntity.ok().body(service.updateItem(userId, item));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return ResponseEntity.ok().body(service.deleteItem(userId, itemId));
    }
}