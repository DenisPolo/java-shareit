package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
    private final long itemId = 1L;
    private final long userId = 1L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testFindItemsNormalConditionFindAll() {
        final Item item1 = mock(Item.class);
        final Item item2 = mock(Item.class);

        when(item1.getId()).thenReturn(1L);
        when(item1.getCreationDate()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30));
        when(item2.getId()).thenReturn(2L);
        when(item2.getCreationDate()).thenReturn(LocalDateTime.of(2023, 2, 2, 10, 30));
        when(itemRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item1, item2)));
        when(bookingRepository.findBookingsForItemIn(anyList())).thenReturn(new ArrayList<>());
        when(commentRepository.findCommentByItemIdIn(anyList())).thenReturn(new ArrayList<>());

        final List<ItemWithBookingsDto> actual = itemService.findItems(null, 0, 10);

        assertNotNull(actual);
        assertEquals(Stream.of(item1, item2)
                .map((i) -> ItemMapper.INSTANCE.mapToItemWithBookingsDto(i, null))
                .peek(i -> i.setComments(null))
                .collect(Collectors.toList()), actual);
        verify(itemRepository).findAll(PageRequest.of(0, 10));
        verify(bookingRepository).findBookingsForItemIn(List.of(1L, 2L));
        verify(commentRepository).findCommentByItemIdIn(List.of(1L, 2L));
    }

    @Test
    void testFindItemsNormalConditionFindByOwner() {
        final Item item1 = mock(Item.class);
        final Item item2 = mock(Item.class);

        when(item1.getId()).thenReturn(1L);
        when(item1.getCreationDate()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30));
        when(item2.getId()).thenReturn(2L);
        when(item2.getCreationDate()).thenReturn(LocalDateTime.of(2023, 2, 2, 10, 30));
        when(itemRepository.findItemsByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(item1, item2));
        when(bookingRepository.findBookingsForItemIn(anyList())).thenReturn(new ArrayList<>());
        when(commentRepository.findCommentByItemIdIn(anyList())).thenReturn(new ArrayList<>());

        final List<ItemWithBookingsDto> actual = itemService.findItems(userId, 0, 10);

        assertNotNull(actual);
        assertEquals(Stream.of(item1, item2)
                .map((i) -> ItemMapper.INSTANCE.mapToItemWithBookingsDto(i, null))
                .peek(i -> i.setComments(null))
                .collect(Collectors.toList()), actual);
        verify(itemRepository).findItemsByOwnerId(userId, PageRequest.of(0, 10));
        verify(bookingRepository).findBookingsForItemIn(List.of(1L, 2L));
        verify(commentRepository).findCommentByItemIdIn(List.of(1L, 2L));
    }

    @Test
    void testFindItemNormalConditionByOwner() {
        final Item item = mock(Item.class);
        final User user = mock(User.class);

        when(item.getAvailable()).thenReturn(true);
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(item.getCreationDate()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsForItem(anyLong())).thenReturn(new ArrayList<>());
        when(commentRepository.findCommentByItemId(anyLong())).thenReturn(new ArrayList<>());

        final ItemWithBookingsDto actual = itemService.findItem(itemId, userId);

        assertNotNull(actual);
        assertEquals(ItemMapper.INSTANCE.mapToItemWithBookingsDto(item, null), actual);
        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(userId);
        verify(commentRepository).findCommentByItemId(itemId);
        verify(bookingRepository).findBookingsForItem(itemId);
    }

    @Test
    void testFindItemNormalConditionByNotOwner() {
        final Item item = mock(Item.class);
        final User user = mock(User.class);

        when(item.getAvailable()).thenReturn(true);
        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
        when(item.getCreationDate()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.findCommentByItemId(anyLong())).thenReturn(new ArrayList<>());

        final ItemWithBookingsDto actual = itemService.findItem(itemId, userId);

        assertNotNull(actual);
        assertEquals(ItemMapper.INSTANCE.mapToItemWithBookingsDto(item, null), actual);
        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(userId);
        verify(commentRepository).findCommentByItemId(itemId);
    }

    @Test
    void testSearchItemsNormalConditionWhenTextNotEmpty() {
        final Item item1 = mock(Item.class);
        final Item item2 = mock(Item.class);

        when(item1.getAvailable()).thenReturn(true);
        when(item2.getAvailable()).thenReturn(true);
        when(itemRepository
                .findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(anyString(), anyString(),
                        any(PageRequest.class))).thenReturn(List.of(item1, item2));

        final List<ItemDto> actual = itemService.searchItems("any_text", 0, 10);

        assertNotNull(actual);
        assertEquals(ItemMapper.INSTANCE.mapToItemDto(List.of(item1, item2)), actual);
        verify(itemRepository)
                .findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("any_text", "any_text",
                        PageRequest.of(0, 10));
    }

    @Test
    void testSearchItemsNormalConditionWhenTextIsEmpty() {

        final List<ItemDto> actual = itemService.searchItems(" ", 0, 10);

        assertNotNull(actual);
        assertEquals(new ArrayList<>(), actual);
        verify(itemRepository, never())
                .findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(anyString(), anyString(),
                        any(PageRequest.class));
    }

    @Test
    void testSaveItemNormalCondition() {
        final ItemCreationDto itemCreationDto = mock(ItemCreationDto.class);
        final User owner = mock(User.class);
        final ItemRequest request = mock(ItemRequest.class);
        final Item item = ItemMapper.INSTANCE.mapToNewItem(itemCreationDto, owner, request);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        final ItemForItemRequestDto actual = itemService.saveItem(userId, itemCreationDto);

        assertNotNull(actual);
        assertEquals(ItemMapper.INSTANCE.mapToItemForItemRequestDto(item), actual);
        verify(userRepository).findById(userId);
        verify(itemRepository).save(item);
    }

    @Test
    void testPostCommentNormalCondition() {
        final Item item = mock(Item.class);
        final Booking booking = mock(Booking.class);
        final CommentCreationDto commentCreationDto = mock(CommentCreationDto.class);
        final Comment comment = mock(Comment.class);
        final User author = mock(User.class);

        when(booking.getStart()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsForItem(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        final CommentDto actual = itemService.postComment(userId, itemId, commentCreationDto);

        assertNotNull(actual);
        assertEquals(CommentMapper.INSTANCE.mapToCommentDto(comment), actual);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingsForItem(itemId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testUpdateItemNormalCondition() {
        final ItemCreationDto itemCreationDto = mock(ItemCreationDto.class);
        final Item updatableItem = new Item();
        final User user = mock(User.class);
        final ItemRequest request = mock(ItemRequest.class);

        when(itemCreationDto.getId()).thenReturn(itemId);
        when(itemCreationDto.getName()).thenReturn("item_name");
        when(itemCreationDto.getDescription()).thenReturn("item_description");
        when(itemCreationDto.getAvailable()).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatableItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatableItem);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        final ItemDto actual = itemService.updateItem(userId, itemId, itemCreationDto);

        assertNotNull(actual);
        assertEquals(ItemMapper.INSTANCE
                .mapToItemDto(new Item(null, null, "item_name", "item_description", true, null, null)), actual);
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(updatableItem);
        verify(userRepository).findById(userId);
    }

    @Test
    void testDeleteItemNormalCondition() {
        final Item item = new Item();
        final User user = mock(User.class);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.findItemByOwnerIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final ResponseFormat actual = itemService.deleteItem(userId, itemId);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatus());
        assertEquals("Вещь с id: 1 успешно удалена", actual.getMessage());
        verify(itemRepository).findById(itemId);
        verify(itemRepository).deleteById(itemId);
        verify(itemRepository).findItemByOwnerIdAndItemId(userId, itemId);
        verify(userRepository).findById(userId);
    }

    @Test
    void testFindItemsThrowsBadRequestExceptionWhenPageFromLessThanZero() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.findItems(null, -1, 10));

        Assertions.assertEquals("Параметр запроса from: -1 не может быть меньше 0", exception.getMessage());
    }

    @Test
    void testFindItemsThrowsBadRequestExceptionWhenSizeFromLessOrEqualsZero() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.findItems(null, 0, 0));

        Assertions.assertEquals("Параметр запроса size: 0 должен быть больше 0", exception.getMessage());
    }

    @Test
    void testFindItemThrowsNotFoundExceptionWhenUserNotOwnerAndItemNotAvailable() {
        final Item item = mock(Item.class);
        final User user = mock(User.class);

        when(item.getId()).thenReturn(itemId);
        when(item.getOwner()).thenReturn(user);
        when(item.getAvailable()).thenReturn(false);
        when(user.getId()).thenReturn(userId);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findItem(2L, itemId));

        Assertions.assertEquals("В данный момент вещь с ID: 1 не доступена", exception.getMessage());
    }

    @Test
    void testFindItemThrowsNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findItem(itemId, userId));

        Assertions.assertEquals("Пользователя с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testFindItemThrowsNotFoundExceptionWhenItemNotExists() {
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findItem(itemId, userId));

        Assertions.assertEquals("Вещь с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testSearchItemsThrowsBadRequestExceptionWhenTextIsNull() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.searchItems(null, 0, 10));

        Assertions.assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

    @Test
    void testPostCommentThrowsNotFoundExceptionWhenUserNotExists() {
        final CommentCreationDto commentCreationDto = mock(CommentCreationDto.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentCreationDto));

        Assertions.assertEquals("Пользователь с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testPostCommentThrowsBadRequestExceptionWhenAnyBookingNotExists() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);
        final CommentCreationDto commentCreationDto = mock(CommentCreationDto.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsForItem(anyLong())).thenReturn(new ArrayList<>());

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.postComment(userId, itemId, commentCreationDto));

        Assertions.assertEquals("Отсутствует завершенная аренда", exception.getMessage());
    }

    @Test
    void testUpdateItemThrowsBadRequestExceptionWhenItemIdInBodyAndUriNotEquals() {
        final ItemCreationDto itemCreationDto = mock(ItemCreationDto.class);

        when(itemCreationDto.getId()).thenReturn(itemId);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.updateItem(userId, 2L, itemCreationDto));

        Assertions.assertEquals("В запросе не совпадают itemId в body и URI:\nPathVariable itemId: 2\nbody itemId: 1",
                exception.getMessage());
    }

    @Test
    void testUpdateItemThrowsBadRequestExceptionWhenItemCreationDtoHasEmptyNameDescriptionAndAvailable() {
        final ItemCreationDto itemCreationDto = mock(ItemCreationDto.class);
        final User user = mock(User.class);
        final Item item = mock(Item.class);

        when(itemCreationDto.getId()).thenReturn(itemId);
        when(itemCreationDto.getAvailable()).thenReturn(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.updateItem(userId, itemId, itemCreationDto));

        Assertions.assertEquals("Выполнен запрос с пустыми полями name, description и available",
                exception.getMessage());
    }

    @Test
    void testDeleteItemThrowsBadRequestExceptionWhenItemDidNotDelete() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.findItemByOwnerIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.deleteItem(userId, itemId));

        Assertions.assertEquals("Вещь с id: 1 удалить не удалось",
                exception.getMessage());
    }
}