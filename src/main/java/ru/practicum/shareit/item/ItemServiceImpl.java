package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemWithBookingsDto> findItems(Long userId) {
        List<Item> items;
        List<Comment> comments;
        List<Booking> bookings;

        if (userId == null) {
            log.info("Запрос списка всех вещей");

            items = itemRepository.findAll();

            comments = commentRepository.findAll();

            bookings = bookingRepository.findAll();
        } else {
            log.info("Запрос списка всех вещей пользователя с ID: " + userId);

            items = itemRepository.findItemsByOwnerId(userId);

            List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

            comments = (itemIds.isEmpty()) ? new ArrayList<>() : commentRepository.findCommentByItemIdIn(itemIds);

            bookings = (itemIds.isEmpty()) ? new ArrayList<>() : bookingRepository.findBookingsForItemIn(itemIds);
        }

        Map<Long, List<CommentDto>> commentsByItemIds = comments.stream()
                .collect(Collectors.groupingBy(Comment::getItemId, Collectors
                        .mapping(CommentMapper::mapToCommentDto, Collectors.toList())));

        Map<Long, List<Booking>> bookingsByItemIds = bookings.stream()
                .collect(Collectors.groupingBy(((b) -> b.getItem().getId())));

        return items
                .stream()
                .map((i) -> ItemMapper.mapToItemWithBookingsDto(i, bookingsByItemIds.get(i.getId())))
                .peek(i -> i.setComments(commentsByItemIds.get(i.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemWithBookingsDto findItem(long userId, long itemId) {
        log.info("Запрос вещи с id: " + itemId + " пользователя с ID: " + userId);

        checkUserExists(userId);

        Item item = getItemIfExists(itemId);

        if (!item.getAvailable() && (item.getOwner().getId() != userId)) {
            String message = "В данныймомент вещь с ID: " + item.getId() + " не доступена";

            log.info(message);

            throw new NotFoundException(message);
        }

        List<CommentDto> comments = commentRepository.findCommentByItemId(itemId)
                .stream().map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());

        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findBookingsForItem(itemId);

            ItemWithBookingsDto itemWithBookingsDto = ItemMapper.mapToItemWithBookingsDto(item, bookings);

            itemWithBookingsDto.setComments(comments);

            return itemWithBookingsDto;
        } else {
            ItemWithBookingsDto itemWithBookingsDto = ItemMapper.mapToItemWithBookingsDto(item, new ArrayList<>());

            itemWithBookingsDto.setComments(comments);

            return itemWithBookingsDto;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по запросу: " + text);

        List<Item> items =
                itemRepository.findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);

        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public ItemDto saveItem(long userId, ItemCreationDto itemCreationDto) {
        log.info("Запрос добавления новой вещи от пользователя с id: " + userId);

        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID: "
                + userId + " не существует"));

        Item item = ItemMapper.mapToNewItem(itemCreationDto, owner);

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto postComment(long userId, long itemId, CommentCreationDto comment) {
        log.info("Запрос добавления комментария от пользователя с id: " + userId + " для вещи с ID: " + itemId);

        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID: "
                + userId + " не существует"));

        getItemIfExists(itemId);

        if (bookingRepository.findBookingsForItem(itemId).stream()
                .noneMatch(b -> b.getStart().isBefore(LocalDateTime.now()))) {
            String message = "Отсутствует завершенная аренда";

            log.info(message);

            throw new BadRequestException(message);
        }

        return CommentMapper.mapToCommentDto(commentRepository.save(new Comment(author, itemId, comment.getText())));
    }

    @Override
    public ItemDto updateItem(long userId, ItemCreationDto itemCreationDto) {
        log.info("Запрос обновления данных вещи с id: " + itemCreationDto.getId() + " от пользователя с id: " + userId);

        checkUserExists(userId);

        Item updatableItem = getItemIfExists(itemCreationDto.getId());

        if ((itemCreationDto.getName() == null) && (itemCreationDto.getDescription() == null)
                && (itemCreationDto.getAvailable() == null)) {
            String message = "Выполнен запрос с пустыми полями name, description и available";

            log.info(message);

            throw new BadRequestException(message);
        }

        if (itemCreationDto.getName() != null) {
            updatableItem.setName(itemCreationDto.getName());
        }

        if (itemCreationDto.getDescription() != null) {
            updatableItem.setDescription(itemCreationDto.getDescription());
        }

        if (itemCreationDto.getAvailable() != null) {
            updatableItem.setAvailable(itemCreationDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.save(updatableItem));
    }

    @Override
    public ResponseFormat deleteItem(long userId, long itemId) {
        checkUserExists(userId);

        itemRepository.deleteById(itemId);

        if (itemRepository.findItemsByOwnerIdAndItemId(userId, itemId).isEmpty()) {
            String message = "Вещь с id: " + itemId + " успешно удалена";

            log.info(message);

            return new ResponseFormat(message, HttpStatus.OK);
        } else {
            String message = "Вещь с id: " + itemId + " удалить не удалось";

            log.warn(message);

            throw new BadRequestException(message);
        }
    }

    private Item getItemIfExists(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            String message = "Вещь с ID: " + itemId + " не существует";

            log.info(message);

            throw new NotFoundException(message);
        }

        return item.get();
    }

    private void checkUserExists(long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            String message = "Пользователя с ID: " + userId + " не существует";

            log.info(message);

            throw new NotFoundException(message);
        }
    }
}