package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping
    public ResponseEntity<List<ItemWithBookingsDto>> findItems(@RequestHeader(name = "X-Sharer-User-Id",
            required = false) Long userId) {
        return ResponseEntity.ok().body(service.findItems(userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingsDto> findItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PathVariable long itemId) {
        return ResponseEntity.ok().body(service.findItem(userId, itemId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(required = false) String text) {
        if (text == null) {
            String message = "Отсутствует параметр запроса";

            log.info(message);

            throw new BadRequestException(message);
        }

        if (text.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        return ResponseEntity.ok().body(service.searchItems(text));
    }

    @PostMapping
    public ResponseEntity<ItemDto> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody ItemCreationDto itemCreationDto) {
        itemCreationDto.setOwnerId(userId);

        return ResponseEntity.ok().body(service.saveItem(userId, itemCreationDto));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> postComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long itemId,
                                                  @Valid @RequestBody CommentCreationDto comment) {
        return ResponseEntity.ok().body(service.postComment(userId, itemId, comment));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId,
                                              @RequestBody ItemCreationDto itemCreationDto) {
        if ((itemCreationDto.getId() != null) && (itemCreationDto.getId() != itemId)) {
            String message = "В запросе не совпадают itemId в body и URI:\nPathVariable itemId: " + itemId +
                    "\nbody itemId: " + itemCreationDto.getId();

            log.info(message);

            throw new BadRequestException(message);
        }

        itemCreationDto.setId(itemId);

        return ResponseEntity.ok().body(service.updateItem(userId, itemCreationDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ResponseFormat> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long itemId) {
        return ResponseEntity.ok().body(service.deleteItem(userId, itemId));
    }
}