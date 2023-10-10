package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.responseFormat.ResponseFormat;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> findUsersItemRequests(Long userId);

    List<ItemRequestDto> findItemRequests(Long userId, Integer from, Integer size);

    ItemRequestDto findItemRequest(Long userId, Long requestId);

    ItemRequestDto createItemRequest(Long userId, ItemRequest itemRequest);

    ResponseFormat deleteItemRequest(Long userId, Long requestId);
}