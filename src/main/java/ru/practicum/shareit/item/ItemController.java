package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ResponseFormat;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping()
    public List<ItemDto> findItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            return service.findAllItems();
        } else {
            return service.findItemsByUserId(userId);
        }
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return service.findItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(required = false) String text) {
        if (text == null) {
            String message = "Отсутствует параметр запроса";
            log.info(message);
            throw new RuntimeException(message);
        }
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return service.searchItems(text);
    }

    @PostMapping()
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody Item item) {
        item.setOwnerId(userId);
        return service.saveItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody Item item) {
        if ((item.getId() != null) && (item.getId() != itemId)) {
            String message = "В запросе не совпадают itemId в body и URI:\nPathVariable itemId: " + itemId +
                    "\nbody itemId: " + item.getId();
            log.info(message);
            throw new RuntimeException();
        }
        item.setId(itemId);
        return service.updateItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseFormat deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return service.deleteItem(userId, itemId);
    }
}