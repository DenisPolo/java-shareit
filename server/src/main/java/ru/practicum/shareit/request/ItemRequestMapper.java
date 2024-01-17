package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(source = "itemRequest.created", target = "created", dateFormat = "yyyy.MM.dd hh:mm:ss")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemForItemRequestDto> items);

    default List<ItemRequestDto> mapToItemRequestDto(Map<ItemRequest, List<ItemForItemRequestDto>> itemRequests) {
        if (itemRequests == null) return new ArrayList<>();

        List<ItemRequestDto> itemRequestsDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests.keySet()) {
            itemRequestsDtos.add(mapToItemRequestDto(itemRequest, itemRequests.get(itemRequest)));
        }

        return itemRequestsDtos.stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }
}