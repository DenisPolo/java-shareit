package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class ItemServiceIntegrationTest {
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemServiceImpl itemService;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "user1", "user1@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        user2 = new User(null, "user2", "user2@yandex.ru", LocalDateTime.of(2023, 2, 2, 12, 0));
        user3 = new User(null, "user3", "user3@yandex.ru", LocalDateTime.of(2023, 3, 3, 12, 0));
        item1 = new Item(null, user1, "item1", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));
        item2 = new Item(null, user2, "item2", "secondItem", true, null, LocalDateTime.of(2023, 2, 2, 20, 0));
        item3 = new Item(null, user3, "item3", "thirdItem", true, null, LocalDateTime.of(2023, 3, 3, 20, 0));
        booking1 = new Booking(null, LocalDateTime.of(2023, 1, 2, 22, 0), LocalDateTime.of(2023, 1, 3, 22, 0), user1,
                item1, BookingStatus.APPROVED, LocalDateTime.of(2023, 1, 2, 12, 0, 0));
        booking2 = new Booking(null, LocalDateTime.of(2023, 2, 3, 22, 0), LocalDateTime.of(2023, 2, 3, 22, 0), user1,
                item2, BookingStatus.APPROVED, LocalDateTime.of(2023, 2, 3, 12, 0, 0));
        booking3 = new Booking(null, LocalDateTime.of(2023, 3, 4, 22, 0), LocalDateTime.of(2023, 3, 4, 22, 0), user1,
                item3, BookingStatus.APPROVED, LocalDateTime.of(2023, 3, 4, 12, 0, 0));
        comment1 = new Comment(null, user1, 1L, "any text 1", LocalDateTime.of(2023, 1, 4, 12, 0, 0));
        comment2 = new Comment(null, user2, 2L, "any text 2", LocalDateTime.of(2023, 2, 5, 12, 0, 0));
        comment3 = new Comment(null, user3, 3L, "any text 3", LocalDateTime.of(2023, 3, 6, 12, 0, 0));

    }

    @Test
    void testFindItemsAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        final List<ItemWithBookingsDto> actual = itemService.findItems(null, 0, 10);

        item1.setId(1L);
        item2.setId(2L);
        item3.setId(3L);
        booking1.setId(1L);
        booking2.setId(2L);
        booking3.setId(3L);
        comment1.setId(1L);
        comment2.setId(2L);
        comment3.setId(3L);

        Map<Long, List<CommentDto>> commentsByItemIds = new HashMap<>();
        commentsByItemIds.put(1L, List.of(CommentMapper.INSTANCE.mapToCommentDto(comment1)));
        commentsByItemIds.put(2L, List.of(CommentMapper.INSTANCE.mapToCommentDto(comment2)));
        commentsByItemIds.put(3L, List.of(CommentMapper.INSTANCE.mapToCommentDto(comment3)));

        Map<Long, List<Booking>> bookingsByItemIds = new HashMap<>();
        bookingsByItemIds.put(1L, List.of(booking1));
        bookingsByItemIds.put(2L, List.of(booking2));
        bookingsByItemIds.put(3L, List.of(booking3));

        final List<ItemWithBookingsDto> expected = Stream.of(item1, item2, item3)
                .map((i) -> ItemMapper.INSTANCE.mapToItemWithBookingsDto(i, bookingsByItemIds.get(i.getId())))
                .peek(i -> i.setComments(commentsByItemIds.get(i.getId())))
                .sorted(Comparator.comparing(ItemWithBookingsDto::getId))
                .collect(Collectors.toList());

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testFindItemsForOwner() {
        item3.setOwner(user2);

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        final List<ItemWithBookingsDto> actual = itemService.findItems(2L, 0, 10);

        item1.setId(1L);
        item2.setId(2L);
        item3.setId(3L);

        final List<ItemWithBookingsDto> expected = Stream.of(item2, item3)
                .map((i) -> ItemMapper.INSTANCE.mapToItemWithBookingsDto(i, null))
                .peek(i -> i.setComments(null))
                .sorted(Comparator.comparing(ItemWithBookingsDto::getId))
                .collect(Collectors.toList());

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testFindItem() {
        item2.setOwner(user1);
        item3.setOwner(user1);
        userRepository.save(user1);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        final ItemWithBookingsDto actual = itemService.findItem(1L, 2L);

        item2.setId(2L);

        final ItemWithBookingsDto expected = ItemMapper.INSTANCE.mapToItemWithBookingsDto(item2, new ArrayList<>());

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testSearchItems() {
        item2.setOwner(user1);
        item3.setOwner(user1);
        userRepository.save(user1);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        final List<ItemDto> actual1 = itemService.searchItems("item1", 0, 10);
        final List<ItemDto> actual2 = itemService.searchItems("item2", 0, 10);
        final List<ItemDto> actual3 = itemService.searchItems("item", 0, 10);

        item1.setId(1L);
        item2.setId(2L);
        item3.setId(3L);

        final List<ItemDto> expected1 = ItemMapper.INSTANCE.mapToItemDto(List.of(item1));
        final List<ItemDto> expected2 = ItemMapper.INSTANCE.mapToItemDto(List.of(item2));
        final List<ItemDto> expected3 = ItemMapper.INSTANCE.mapToItemDto(List.of(item1, item2, item3));

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
    }

    @Test
    void testSaveItem() {
        userRepository.save(user1);

        final ItemForItemRequestDto actual = itemService
                .saveItem(1L, new ItemCreationDto(null, null, "itemSaved", "itemSavedDescription", true, null));

        final ItemForItemRequestDto expected = ItemMapper.INSTANCE
                .mapToItemForItemRequestDto(new Item(1L, user1, "itemSaved",
                        "itemSavedDescription", true, null, LocalDateTime.now()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testPostComment() {
        userRepository.save(user1);
        itemRepository.save(item1);
        user1.setId(1L);
        item1.setId(1L);

        final Booking booking = new Booking(null,
                LocalDateTime.of(2023, 1, 2, 12, 0),
                LocalDateTime.of(2023, 1, 3, 12, 0),
                user1,
                item1,
                BookingStatus.APPROVED,
                LocalDateTime.of(2023, 1, 2, 10, 0));

        bookingRepository.save(booking);

        final CommentDto actual = itemService.postComment(1L, 1L, new CommentCreationDto("CommentText"));

        final CommentDto expected = CommentMapper.INSTANCE
                .mapToCommentDto(new Comment(1L, user1, 1L, "CommentText", LocalDateTime.now()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateItem() {
        userRepository.save(user1);
        itemRepository.save(item1);
        user1.setId(1L);
        item1.setId(1L);

        final ItemDto actual = itemService
                .updateItem(1L, 1L, new ItemCreationDto(null, 1L, "itemUpdate", "itemUpdateDescription", true, null));

        final ItemDto expected = ItemMapper.INSTANCE.mapToItemDto(new Item(1L, user1,
                "itemUpdate", "itemUpdateDescription", true, null, LocalDateTime.now()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteItem() {
        userRepository.save(user1);
        itemRepository.save(item1);
        user1.setId(1L);
        item1.setId(1L);

        final ResponseFormat actual = itemService.deleteItem(1L, 1L);

        final ResponseFormat expected = new ResponseFormat("Вещь с id: 1 успешно удалена", HttpStatus.OK);

        assertNotNull(actual);
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMessage(), actual.getMessage());
    }
}