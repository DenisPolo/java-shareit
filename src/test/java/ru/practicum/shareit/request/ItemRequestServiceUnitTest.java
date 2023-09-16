package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {
    private final long userId = 1L;
    private final long requestId = 1L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void testFindUsersItemRequestsNormalCondition() {
        final User user = mock(User.class);
        final Item item1 = mock(Item.class);
        final Item item2 = mock(Item.class);
        final Item item3 = mock(Item.class);
        final Item item4 = mock(Item.class);
        final ItemRequest itemRequest1 = mock(ItemRequest.class);
        final ItemRequest itemRequest2 = mock(ItemRequest.class);

        when(item1.getRequest()).thenReturn(itemRequest1);
        when(item2.getRequest()).thenReturn(itemRequest1);
        when(item3.getRequest()).thenReturn(itemRequest2);
        when(item4.getRequest()).thenReturn(itemRequest2);
        when(itemRequest1.getId()).thenReturn(requestId);
        when(itemRequest1.getCreated()).thenReturn(Instant.now());
        when(itemRequest2.getId()).thenReturn(requestId + 1);
        when(itemRequest2.getCreated()).thenReturn(Instant.now().plusSeconds(60 * 60 * 24));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findItemRequestsByUserId(anyLong())).thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findItemsByRequestIdIn(anyList())).thenReturn(List.of(item1, item2, item3, item4));

        final List<ItemRequestDto> actual = itemRequestService.findUsersItemRequests(userId);

        Map<ItemRequest, List<ItemForItemRequestDto>> itemRequests = new HashMap<>();
        itemRequests.put(itemRequest1, ItemMapper.INSTANCE.mapToItemForItemRequestDto(List.of(item1, item2)));
        itemRequests.put(itemRequest2, ItemMapper.INSTANCE.mapToItemForItemRequestDto(List.of(item3, item4)));

        assertNotNull(actual);
        assertEquals(ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequests), actual);
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findItemRequestsByUserId(userId);
        verify(itemRepository).findItemsByRequestIdIn(List.of(userId, 2L));
    }

    @Test
    void testFindItemRequestsNormalCondition() {
        final User user = mock(User.class);
        final ItemRequest itemRequest1 = mock(ItemRequest.class);
        final ItemRequest itemRequest2 = mock(ItemRequest.class);

        when(itemRequest1.getId()).thenReturn(requestId);
        when(itemRequest1.getCreated()).thenReturn(Instant.now());
        when(itemRequest2.getId()).thenReturn(requestId + 1);
        when(itemRequest2.getCreated()).thenReturn(Instant.now().plusSeconds(60 * 60 * 24));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findItemRequestsByUserIdNot(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findItemsByRequestIdIn(anyList())).thenReturn(new ArrayList<>());

        final List<ItemRequestDto> actual = itemRequestService.findItemRequests(userId, 0, 10);

        Map<ItemRequest, List<ItemForItemRequestDto>> itemRequests = new HashMap<>();
        itemRequests.put(itemRequest1, new ArrayList<>());
        itemRequests.put(itemRequest2, new ArrayList<>());

        assertNotNull(actual);
        assertEquals(ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequests), actual);
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findItemRequestsByUserIdNot(userId, PageRequest.of(0, 10));
        verify(itemRepository).findItemsByRequestIdIn(List.of(userId, 2L));
    }

    @Test
    void testFindItemRequestNormalCondition() {
        final User user = mock(User.class);
        final ItemRequest itemRequest = mock(ItemRequest.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findItemsByRequestId(anyLong())).thenReturn(new ArrayList<>());
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        final ItemRequestDto actual = itemRequestService.findItemRequest(userId, requestId);

        assertNotNull(actual);
        assertEquals(ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequest, new ArrayList<>()), actual);
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findItemsByRequestId(requestId);
    }

    @Test
    void testCreateItemRequestNormalCondition() {
        final User user = mock(User.class);
        final ItemRequest itemRequest = mock(ItemRequest.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        final ItemRequestDto actual = itemRequestService.createItemRequest(userId, itemRequest);

        assertNotNull(actual);
        assertEquals(ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequest, new ArrayList<>()), actual);
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void testDeleteItemRequestNormalCondition() {
        final User user = mock(User.class);
        final ItemRequest itemRequest = mock(ItemRequest.class);

        when(itemRequest.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest))
                .thenReturn(Optional.empty());

        final ResponseFormat actual = itemRequestService.deleteItemRequest(userId, requestId);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatus());
        assertEquals("Запрос вещи с id: 1 успешно удален", actual.getMessage());
        verify(userRepository).findById(userId);
        verify(itemRequestRepository, times(2)).findById(requestId);
        verify(itemRequestRepository).deleteById(requestId);
    }

    @Test
    void testFindUsersItemRequestsThrowsNotFoundExceptionWhenUserNotExists() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findUsersItemRequests(requestId));

        Assertions.assertEquals("Пользователя с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testFindItemRequestsThrowsBadRequestExceptionWhenFromLessThanZero() {
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.findItemRequests(requestId, -1, 10));

        Assertions.assertEquals("Параметр запроса from: -1 не может быть меньше 0", exception.getMessage());
    }

    @Test
    void testFindItemRequestsThrowsBadRequestExceptionWhenSizeLessOrEqualsZero() {
        final User user = mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.findItemRequests(requestId, 0, 0));

        Assertions.assertEquals("Параметр запроса size: 0 должен быть больше 0", exception.getMessage());
    }

    @Test
    void testFindItemRequestThrowsNotFoundExceptionWhenSizeLessOrEqualsZero() {
        final User user = mock(User.class);
        final Item item = mock(Item.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findItemsByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findItemRequest(userId, requestId));

        Assertions.assertEquals("Запрос с ID: 1 не существует", exception.getMessage());
    }

    @Test
    void testDeleteItemRequestThrowsBadRequestExceptionWhenUserIsNotAuthor() {
        final User user = mock(User.class);
        final ItemRequest itemRequest = mock(ItemRequest.class);

        when(itemRequest.getUser()).thenReturn(user);
        when(itemRequest.getUser().getId()).thenReturn(userId + 1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.deleteItemRequest(userId, requestId));

        Assertions.assertEquals("Пользователь с id: 1 не является автором запроса с id: 1", exception.getMessage());
    }

    @Test
    void testDeleteItemRequestThrowsBadRequestExceptionWhenItemDidNotDelete() {
        final User user = mock(User.class);
        final ItemRequest itemRequest = mock(ItemRequest.class);

        when(itemRequest.getUser()).thenReturn(user);
        when(itemRequest.getUser().getId()).thenReturn(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.deleteItemRequest(userId, requestId));

        Assertions.assertEquals("Запрос вещи с id: 1 удалить не удалось", exception.getMessage());
    }
}