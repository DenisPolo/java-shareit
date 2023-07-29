package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ResponseFormat;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAllItems();

    List<ItemDto> findItemsByUserId(long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> searchItems(String text);

    ItemDto saveItem(long userId, Item item);

    ItemDto updateItem(long userId, Item item);

    ResponseFormat deleteItem(long userId, long itemId);
}