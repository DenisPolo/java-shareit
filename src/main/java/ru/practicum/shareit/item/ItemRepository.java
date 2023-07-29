package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

interface ItemRepository {
    List<ItemDto> findAllItems();

    List<ItemDto> findItemsByUserId(long userId);

    Optional<ItemDto> findItem(long itemId);

    List<ItemDto> searchItems(String text);

    ItemDto saveItem(Item item);

    ItemDto updateItem(long userId, Item item);

    boolean deleteItem(long userId, long itemId);
}