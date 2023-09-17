package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class ItemRequestServiceIntegrationTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;
    private ItemRequest request4;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "user1", "user1@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));
        user2 = new User(null, "user2", "user2@yandex.ru", LocalDateTime.of(2023, 2, 2, 12, 0));
        user3 = new User(null, "user3", "user3@yandex.ru", LocalDateTime.of(2023, 3, 3, 12, 0));
        user4 = new User(null, "user4", "user4@yandex.ru", LocalDateTime.of(2023, 4, 4, 12, 0));
        request1 = new ItemRequest(null, user1, "item_request_description_1",
                LocalDateTime.of(2023, 1, 1, 22, 0).toInstant(ZoneOffset.UTC));
        request2 = new ItemRequest(null, user2, "item_request_description_2",
                LocalDateTime.of(2023, 2, 2, 22, 0).toInstant(ZoneOffset.UTC));
        request3 = new ItemRequest(null, user3, "item_request_description_3",
                LocalDateTime.of(2023, 3, 3, 22, 0).toInstant(ZoneOffset.UTC));
        request4 = new ItemRequest(null, user4, "item_request_description_4",
                LocalDateTime.of(2023, 4, 4, 22, 0).toInstant(ZoneOffset.UTC));
    }

    @Test
    void testFindUsersItemRequests() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        itemRequestRepository.save(request3);
        itemRequestRepository.save(request4);

        final List<ItemRequestDto> actual = itemRequestService.findUsersItemRequests(2L);
        final List<ItemRequestDto> expected = ItemRequestMapper.INSTANCE
                .mapToItemRequestDto(Map.of(request2, new ArrayList<>()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testFindItemRequests() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        itemRequestRepository.save(request3);
        itemRequestRepository.save(request4);

        final List<ItemRequestDto> actual = itemRequestService.findItemRequests(2L, 0, 10);
        final List<ItemRequestDto> expected = ItemRequestMapper.INSTANCE
                .mapToItemRequestDto(Map.of(request1, new ArrayList<>(), request3, new ArrayList<>(), request4, new ArrayList<>()));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testFindItemRequest() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        itemRequestRepository.save(request3);

        final ItemRequestDto actual1 = itemRequestService.findItemRequest(1L, 1L);
        final ItemRequestDto actual2 = itemRequestService.findItemRequest(3L, 3L);
        final ItemRequestDto expected1 = ItemRequestMapper.INSTANCE.mapToItemRequestDto(request1, new ArrayList<>());
        final ItemRequestDto expected2 = ItemRequestMapper.INSTANCE.mapToItemRequestDto(request3, new ArrayList<>());

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void testCreateItemRequest() {
        userRepository.save(user1);

        final ItemRequestDto actual = itemRequestService.createItemRequest(1L, request1);
        final ItemRequestDto expected = ItemRequestMapper.INSTANCE.mapToItemRequestDto(request1, new ArrayList<>());

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteItemRequest() {
        userRepository.save(user1);
        itemRequestRepository.save(request1);

        final ItemRequestDto actual1 = itemRequestService.createItemRequest(1L, request1);
        final ItemRequestDto expected1 = ItemRequestMapper.INSTANCE.mapToItemRequestDto(request1, new ArrayList<>());
        final ResponseFormat actual2 = itemRequestService.deleteItemRequest(1L, 1L);
        final ResponseFormat expected2 = new ResponseFormat("Запрос вещи с id: 1 успешно удален", HttpStatus.OK);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertEquals(expected1, actual1);
        assertEquals(expected2.getStatus(), actual2.getStatus());
        assertEquals(expected2.getMessage(), actual2.getMessage());
    }
}