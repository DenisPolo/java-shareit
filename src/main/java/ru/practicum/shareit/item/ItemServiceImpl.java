package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ResponseFormat;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findAllItems() {
        log.info("Запрос списка всех вещей");
        return itemRepository.findAllItems();
    }

    @Override
    public List<ItemDto> findItemsByUserId(long userId) {
        log.info("Запрос списка всех вещей пользователя с id: " + userId);
        checkUserExists(userId);
        return itemRepository.findItemsByUserId(userId);
    }

    @Override
    public ItemDto findItem(long itemId) {
        log.info("Запрос вещи с id: " + itemId);
        return itemRepository.findItem(itemId).get();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по запросу: " + text);
        return itemRepository.searchItems(text);
    }

    @Override
    public ItemDto saveItem(long userId, Item item) {
        log.info("Запрос добавления новой вещи от  пользователя с id: " + userId);
        checkUserExists(userId);
        item.setOwnerId(userId);
        return itemRepository.saveItem(item);
    }

    @Override
    public ItemDto updateItem(long userId, Item item) {
        log.info("Запрос обновления данных вещи с id: " + item.getId() + " от пользователя с id: " + userId);
        checkUserExists(userId);
        if ((item.getName() == null) && (item.getDescription() == null) && (item.getAvailable() == null)) {
            String message = "Выполнен запрос с пустыми полями name, description и available";
            log.info(message);
            throw new RuntimeException();
        }
        return itemRepository.updateItem(userId, item);
    }

    @Override
    public ResponseFormat deleteItem(long userId, long itemId) {
        checkUserExists(userId);
        if (itemRepository.deleteItem(userId, itemId)) {
            String message = "Вещь с id: " + itemId + " успешно удалена";
            log.info(message);
            return new ResponseFormat(message, HttpStatus.OK);
        } else {
            String message = "Вещь с id: " + itemId + " удалить не удалось";
            log.warn(message);
            throw new RuntimeException();
        }
    }

    private void checkUserExists(long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            String message = "Пользователь с ID: " + userId + " не существует";
            log.info(message);
            throw new NotFoundException(message);
        }
    }
}