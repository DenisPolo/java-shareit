package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> findItems(Long userId);

    ItemWithBookingsDto findItem(long userId, long itemId);

    List<ItemDto> searchItems(String text);

    ItemDto saveItem(long userId, ItemCreationDto itemCreationDto);

    CommentDto postComment(long userId, long itemId, CommentCreationDto comment);

    ItemDto updateItem(long userId, long itemId, ItemCreationDto itemCreationDto);

    ResponseFormat deleteItem(long userId, long itemId);
}