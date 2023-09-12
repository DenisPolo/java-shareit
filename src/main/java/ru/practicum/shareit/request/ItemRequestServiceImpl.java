package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> findUsersItemRequests(Long userId) {
        log.info("Запрос поиска запросов вещей пользователя с id: " + userId);

        checkUserExists(userId);

        List<ItemRequest> requests = itemRequestRepository.findItemRequestsByUserId(userId);

        return ItemRequestMapper.INSTANCE.mapToItemRequestDto(getMapItemRequestsWithItemDtos(requests));
    }

    @Override
    public List<ItemRequestDto> findItemRequests(Long userId, Integer from, Integer size) {
        log.info("Запрос поиска запросов вещей");

        checkUserExists(userId);

        if (from < 0) {
            String message = "Параметр запроса from: " + from + " не может быть меньше 0";

            log.info(message);

            throw new BadRequestException(message);
        }

        if (size <= 0) {
            String message = "Параметр запроса size: " + size + " должен быть больше 0";

            log.info(message);

            throw new BadRequestException(message);
        }

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<ItemRequest> requests = itemRequestRepository.findItemRequestsByUserIdNot(userId, page);

        return ItemRequestMapper.INSTANCE.mapToItemRequestDto(getMapItemRequestsWithItemDtos(requests));
    }

    @Override
    public ItemRequestDto findItemRequest(Long userId, Long requestId) {
        log.info("Запрос поиска запроса вещи с id: " + requestId);

        checkUserExists(userId);

        List<ItemForItemRequestDto> items = ItemMapper.INSTANCE
                .mapToItemForItemRequestDto(itemRepository.findItemsByRequestId(requestId));

        return ItemRequestMapper.INSTANCE
                .mapToItemRequestDto(getItemRequestIfExists(requestId), items);
    }

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequest itemRequest) {
        log.info("Запрос добавления запроса вещи от пользователя с id: " + userId);

        checkUserExists(userId);

        itemRequest.setUserId(userId);

        return ItemRequestMapper.INSTANCE
                .mapToItemRequestDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public ResponseFormat deleteItemRequest(Long userId, Long requestId) {
        checkUserExists(userId);

        ItemRequest request = getItemRequestIfExists(requestId);

        if (!request.getUserId().equals(userId)) {
            String message = "Пользователь с id: " + userId + " не является автором запроса с id: " + requestId;

            log.info(message);

            throw new BadRequestException(message);
        }

        itemRequestRepository.deleteById(requestId);

        if (itemRequestRepository.findById(requestId).isEmpty()) {
            String message = "Запрос вещи с id: " + requestId + " успешно удален";

            log.info(message);

            return new ResponseFormat(message, HttpStatus.OK);
        } else {
            String message = "Запрос вещи с id: " + requestId + " удалить не удалось";

            log.warn(message);

            throw new BadRequestException(message);
        }
    }

    private Map<ItemRequest, List<ItemForItemRequestDto>> getMapItemRequestsWithItemDtos(List<ItemRequest> requests) {
        List<Long> requestsIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());

        List<ItemForItemRequestDto> items = ItemMapper.INSTANCE
                .mapToItemForItemRequestDto(itemRepository.findItemsByRequestIdIn(requestsIds));

        Map<ItemRequest, List<ItemForItemRequestDto>> requestsWithItems = new HashMap<>();

        for (ItemRequest request : requests) {
            List<ItemForItemRequestDto> itemsForRequest = items.stream()
                    .filter(i -> i.getRequestId().equals(request.getId()))
                    .collect(Collectors.toList());

            requestsWithItems.put(request, itemsForRequest);
        }

        return requestsWithItems;
    }

    private ItemRequest getItemRequestIfExists(long requestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);

        if (itemRequest.isEmpty()) {
            String message = "Запрос с ID: " + requestId + " не существует";

            log.info(message);

            throw new NotFoundException(message);
        }

        return itemRequest.get();
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