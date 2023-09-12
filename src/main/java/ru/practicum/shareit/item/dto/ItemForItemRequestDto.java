package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ItemForItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private String creationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemForItemRequestDto itemDto = (ItemForItemRequestDto) o;
        return Objects.equals(id, itemDto.id)
                && Objects.equals(name, itemDto.name)
                && Objects.equals(description, itemDto.description)
                && Objects.equals(requestId, itemDto.requestId)
                && Objects.equals(available, itemDto.available);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, requestId, available);
    }
}