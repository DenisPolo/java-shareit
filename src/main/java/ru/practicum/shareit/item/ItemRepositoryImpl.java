package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    long userId;
    private final List<Item> items = new ArrayList<>();

    @Override
    public List<ItemDto> findAllItems() {
        return items.stream().filter(Item::getAvailable).map(this::convertItemToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByUserId(long userId) {
        return items.stream().filter(i -> (i.getOwnerId() == userId) && i.getAvailable())
                .map(this::convertItemToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> findItem(long itemId) {
        if (items.stream().filter(i -> i.getId() == itemId).findFirst().isEmpty()) {
            String message = "Вещь с ID: " + itemId + " не существует";
            log.info(message);
            throw new NotFoundException(message);
        }
        return items.stream().filter(i -> (i.getId() == itemId) && i.getAvailable())
                .map(this::convertItemToDto).findFirst();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return items.stream().filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())) && i.getAvailable())
                .map(this::convertItemToDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto saveItem(Item item) {
        userId++;
        item.setId(userId);
        items.add(item);
        return convertItemToDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, Item item) {
        checkItemExists(userId, item.getId());
        Item updatedItem = items.stream()
                .filter(i -> (i.getOwnerId() == userId) && (i.getId().equals(item.getId())))
                .findFirst()
                .get();
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return convertItemToDto(updatedItem);
    }

    @Override
    public boolean deleteItem(long userId, long itemId) {
        checkItemExists(userId, itemId);
        return items.removeIf(item -> (item.getOwnerId() == userId) && (item.getId() == itemId));
    }

    private ItemDto convertItemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    private void checkItemExists(long userId, long itemId) {
        if (items.stream().filter(i -> (i.getOwnerId() == userId) && (i.getId() == itemId)).findFirst().isEmpty()) {
            String message = "Вещь с ID: " + itemId + " для пользователя с ID: " + userId + "  не существует";
            log.info(message);
            throw new NotFoundException(message);
        }
    }
}